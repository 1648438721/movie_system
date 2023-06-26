package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Role;
import com.system.entity.RoleMenu;
import com.system.entity.UserRole;
import com.system.service.RoleMenuService;
import com.system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-10
 */
@RestController
@RequestMapping("system/role")
public class RoleController extends BaseController {
    @Autowired
    RoleMenuService roleMenuService;
    @Autowired
    UserRoleService userRoleService;

    @Transactional
    @PreAuthorize("hasAuthority('sys:role:perm')")
    @PostMapping("/perm/{roleId}")
    public Result perm(@PathVariable("roleId") Long roleId,@RequestBody Long[] menuIds){
        List<RoleMenu> roleMenuList = new ArrayList<>();
        Arrays.stream(menuIds).forEach(mune_id->{
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(mune_id);
            roleMenuList.add(rm);
        });
        //先将当前这个角色权限清空，从sys_role_menu关联表李清空
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id",roleId));
        //录入新权限数据
        roleMenuService.saveBatch(roleMenuList);

        //角色权限数据已经修改，清空redis中当前角色的缓存数据
        userService.clearUserAuthorityInfoByRoleId(roleId);
        return Result.success("");
    }

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result list(String name){//搜索栏输入的
//        List<Role> roles = roleService.list();
        //情况一：name==null 没有输入模糊查询，查询所有角色的分页信息
        //情况二：name==XXXX输入关键词，按照关键词模糊查询出所有角色的分页数据
        
        Page<Role> roles = roleService.page(getPage(),new QueryWrapper<Role>().like(StrUtil.isNotBlank(name),"name",name));
        return Result.success(roles);
    }

    //编辑角色的方法。 1.请求编辑角色数据  2.显示对话框，修改保存
    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/roleinfo/{id}")
    public Result info(@PathVariable("id") Long id){
        Role role = roleService.getById(id);

        List<RoleMenu> rolemenu = roleMenuService.list(new QueryWrapper<RoleMenu>().eq("role_id", id));
        List<Long> menuIds = rolemenu.stream().map(rm -> rm.getMenuId()).collect(Collectors.toList());

        role.setMenuIds(menuIds);
        return Result.success(role);
    }

    @PreAuthorize("hasAuthority('sys:role:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Role role){
        role.setUpdated(LocalDateTime.now());
        roleService.updateById(role);
        return Result.success("");
    }
    @PreAuthorize("hasAuthority('sys:role:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Role role){
        role.setStatu(Const.STATUS_ON);
        role.setCreated(LocalDateTime.now());
        roleService.save(role);
        return Result.success("");
    }

    //删除
    @Transactional
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        //同步删除其他关联信息
        //ids可能是多个角色编号，不能用eq使用in
        roleMenuService.remove(new QueryWrapper<RoleMenu>().in("role_id",ids));
        userRoleService.remove(new QueryWrapper<UserRole>().in("role_id",ids));

        //ids中就是要删除的角色
        //mabits-Plus封装的批量删除方法，需要集合类
        roleService.removeByIds(Arrays.asList(ids));
        //从缓存中清除相关的用户权限
        return Result.success("");
    }
}
