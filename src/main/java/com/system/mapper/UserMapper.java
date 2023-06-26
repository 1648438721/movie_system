package com.system.mapper;

import com.system.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
public interface UserMapper extends BaseMapper<User> {
    //自定义查询：根据userId用户编号，查询该用户所能操作的菜单id集合
    public List<Long> getNavMenuIds(Long userId);

//    根据菜单编号menuId查询出与该菜单关联方人所有用户信息
    public List<User> listByMenuId(Long menuId);

}
