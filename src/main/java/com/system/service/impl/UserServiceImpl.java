package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Menu;
import com.system.entity.Role;
import com.system.entity.User;
import com.system.mapper.UserMapper;
import com.system.service.MenuService;
import com.system.service.RoleService;
import com.system.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleService roleService;
    @Autowired
    MenuService menuService;
    @Autowired
    RedisUtil redisUtil;
//根据用户名查询该用户的详细信息，用户名不能重复
    public User getUserByUsername(String username){
        QueryWrapper qw = new QueryWrapper();
        qw.eq("username",username);
        return userMapper.selectOne(qw);
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {
        //权限字符串
        String authorityString = "";

        User user = this.getById(userId);
        if (redisUtil.hasKey("granted"+user.getUsername())){
            authorityString = (String) redisUtil.get("granted" + user.getUsername());
        }else{
            //通过登录用户的编号UserId获得该角色的所有信息
            QueryWrapper role_wraper = new QueryWrapper();
            role_wraper.inSql("id","select role_id from sys_user_role where user_id="+userId);
            List<Role> roles = roleService.list(role_wraper);

            //通过登录用户的编号UserId可以查询该用户所能操作的所有菜单id
            List<Long> menuIds = userMapper.getNavMenuIds(userId);
            //使用menuIds中所有的菜单编号menuID，查询所有的菜单的数据
            List<Menu> menus = menuService.listByIds(menuIds);

            //将上面的角色的信息 和 菜单信息 转换为权限字符串( 角色信息和权限信息 ):
            if (roles.size()>0){
                String roleString = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                authorityString = roleString.concat(",");
            }

            if (menus.size()>0){
                String permString = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));

                authorityString = authorityString.concat(permString);
                log.info("权限字符串：{}",authorityString);
            }

            //查询出的权限字符串存储到Redis中
            redisUtil.set("granted"+user.getUsername(),authorityString);
        }
        return authorityString;
    }
    //删除掉redis缓存中某个用户的权限
    @Override
    public void clearUserAuthorityInfo(String username) {
        redisUtil.del("granted"+username);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleID) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.inSql("id","select user_id from sys_user_role where role_id = "+roleID);
        List<User> users = this.list(qw);//查询出关联到删除角色的用户信息

        //根据查询出的用户信息，在redis中将这些用户的权限信息删除
        users.forEach(u ->{
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }

    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuID) {
        List<User> users = userMapper.listByMenuId(menuID);
        //根据查询出的用户信息，在redis中将这些用户的权限信息删除。
        users.forEach(u ->{
            this.clearUserAuthorityInfo(u.getUsername());
        });

    }


}
