package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Menu;
import com.system.entity.User;
import com.system.entity.dto.MenuDto;
import com.system.mapper.MenuMapper;
import com.system.mapper.UserMapper;
import com.system.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    //根据登录用户，获得该用户所能操作的菜单数据
    @Override
    public List<MenuDto> getCurrentUserNav(String username) {
        //根据用户名查询目前的用户信息
        User user = userService.getUserByUsername(username);
        //根据用户编号user.getId() 查询该用户所能操作的菜单id集合
        List<Long> menuIds = userMapper.getNavMenuIds(user.getId());
        //根据获得菜单id的集合，获得具体的菜单数据集合
        List<Menu> menus = this.listByIds(menuIds);
        List<Menu> finalMenus = this.buildTreeMenu(menus);//finalMenu存储的就是带有子菜单 的父菜单集合
//        System.out.println(finalMenus);

        //将集合中Menu 转换为 MenuDto类型
        return this.convert(finalMenus);
    }

    @Override
    public List<Menu> tree() {
        //获得当前的用户所有菜单数据
        List<Menu> menuList = this.list(new QueryWrapper<Menu>().orderByAsc("orderNum"));
        return this.buildTreeMenu(menuList);
    }

    //将集合中Menu 转换为 MenuDto类型
    private List<MenuDto> convert(List<Menu> menus){
        List<MenuDto> menuDtoList = new ArrayList<>();
        menus.forEach(m->{
            MenuDto dto = new MenuDto();
            dto.setId(m.getId());
            dto.setName(m.getPerms());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setIcon(m.getIcon());
            dto.setPath(m.getPath());
            if (m.getChildren().size()>0){//当前菜单是有子菜单
            //menu菜单取出Children有是一个 Menu集合（子菜单也需要Menu转为MenuDto）。
            //递归调用，convert(子菜单集合) ，将子菜单再转换一下。
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtoList.add(dto);
        });
        return menuDtoList;
    }

    //菜单数据转化为Tree结构，分为一级菜单和二级菜单
    public List<Menu> buildTreeMenu(List<Menu> menus){
        List<Menu> finalMenus = new ArrayList<>();
        for (Menu m : menus){
            for (Menu e : menus){
                if (e.getParentId() == m.getId()){
                    m.getChildren().add(e);//e菜单添加到m菜单，e就是m的子菜单
                }
            }
            if (m.getParentId() == 0L){
                finalMenus.add(m);
            }
        }
        return finalMenus;
    }

}
