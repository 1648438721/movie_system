package com.system.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.common.Result;
import com.system.entity.Menu;
import com.system.entity.RoleMenu;
import com.system.entity.User;
import com.system.entity.dto.MenuDto;
import com.system.service.MenuService;
import com.system.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController extends BaseController {
    @Autowired
    RoleMenuService roleMenuService;

    //根据id获得菜单详细信息
    @PreAuthorize("hasAuthority('sys:menu:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }


    //更新菜单
    @PreAuthorize("hasAuthority('sys:menu:update')")
    @RequestMapping("/update")
    //如果前端亲求的参数是一个JSON对象，那么接口方法的参数必须使用@RequestBody
    public Result update(@RequestBody Menu menu){
        menu.setUpdated(LocalDateTime.now());
        menuService.updateById(menu);
        return Result.success(menu);
    }
    //添加新建菜单信息
    @PreAuthorize("hasAuthority('sys:menu:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Menu menu){
        menu.setCreated(LocalDateTime.now());
        menuService.save(menu);
        return Result.success(menu);
    }


    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        //查询要删除的菜单id下要删除的子菜单数量
        int count = menuService.count(new QueryWrapper<Menu>().eq("parent_id", id));
        if (count>0){
            return Result.fail("请先删除子菜单");
        }
        //清空redis中关联的权限
        userService.clearUserAuthorityInfoByMenuId(id);

        menuService.removeById(id);
        //删除菜单，sys_role_menu中还有角色和菜单关联的数据需要同步删除
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("menu_id",id));
        return Result.success("");
    }
    //获得菜单页面加载时表格的菜单数据：
    @PreAuthorize("hasAuthority('sys:menu:list')")
    @RequestMapping("/list")
    public Result list(){
        List<Menu> tree = menuService.tree();
        return Result.success(tree);
    }




}
