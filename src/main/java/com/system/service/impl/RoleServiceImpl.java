package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Role;
import com.system.entity.UserRole;
import com.system.mapper.RoleMapper;
import com.system.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.UserRoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    UserRoleService userRoleService;
    @Override
    public List<Role> listRolesByUserId(Long userId) {
        List<UserRole> user_role_list = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));
        //role_id,user_id
        if(user_role_list.size()>0){
            List<Long> roleIds = user_role_list.stream().map(ur-> ur.getRoleId()).collect(Collectors.toList()); //从关联对象中，只取出role_id
            List<Role> roles = this.listByIds(roleIds);
            return roles;
        }else{
            return null;
        }
    }

}
