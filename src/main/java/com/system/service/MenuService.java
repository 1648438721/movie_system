package com.system.service;

import com.system.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.entity.dto.MenuDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
public interface MenuService extends IService<Menu> {
//根据登录用户，获得该用户所能操作的菜单数据
    public List<MenuDto> getCurrentUserNav(String username);
    //获得菜单首页数据，返回父子关系的菜单介绍
    public List<Menu> tree();
}
