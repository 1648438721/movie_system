package com.system.service;

import com.system.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
public interface UserService extends IService<User> {
    public User getUserByUsername(String username);

    public String getUserAuthorityInfo(Long userId);

    //删除某个用户的缓存中的权限信息
    public void clearUserAuthorityInfo(String username);
    //删除所有与该角色关联的用户的权限
    public void clearUserAuthorityInfoByRoleId(Long roleID);
    //删除所有与某菜单关联的所有用户的权限信息
    public void  clearUserAuthorityInfoByMenuId(Long menuID);
}
