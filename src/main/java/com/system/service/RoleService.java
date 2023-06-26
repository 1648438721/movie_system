package com.system.service;

import com.system.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
public interface RoleService extends IService<Role> {
    public List<Role> listRolesByUserId(Long userId);
}
