package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Role;
import com.system.entity.RoleMenu;
import com.system.entity.User;
import com.system.entity.UserRole;
import com.system.entity.dto.PassDto;
import com.system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
@RequestMapping("/system/user")
public class UserController extends BaseController {
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('sys:user:setpass')")
    @PostMapping("/updatePass")
    public Result updatePass(@RequestBody PassDto passDto, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        //修改密码之前验证旧密码
        boolean matches = passwordEncoder.matches(passDto.getPassword(), user.getPassword());
        if (!matches){
            return Result.fail("旧密码不正确");
        }
        //修改密码进行加密

        user.setPassword(passwordEncoder.encode(passDto.getNewpass()));
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.success(null);
    }

    @PreAuthorize("hasAuthority('sys:user:role')")
    @PostMapping("/role/{id}")
    public Result userRole(@PathVariable("id") Long id,@RequestBody Long[] roleIds){
        List<UserRole> userRoleList = new ArrayList<>();
        Arrays.stream(roleIds).forEach(r->{
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(r);
            userRoleList.add(userRole);
        });

        //先删除用户现有角色信息
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id",id));
        //保存用户新分配的角色数据
        userRoleService.saveBatch(userRoleList);
        //删除redis中缓存权限数据
        User user = userService.getById(id);
        userService.clearUserAuthorityInfo(user.getUsername());
        return Result.success(null);
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/list")
    public Result list(String username){
        Page<User> users = userService.page(getPage(), new QueryWrapper<User>().like(StrUtil.isNotBlank(username), "username", username));
//查询该用户所具有的角色的信息
        users.getRecords().stream().forEach(u->{
            List<Role> roles = roleService.listRolesByUserId(u.getId());
            u.setRoles(roles);
        });
        return Result.success(users);
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/userinfo/{username}")
    public Result getUserByUsername(@PathVariable("username") String username){
        User user = userService.getUserByUsername(username);
        if (user!=null){
            return Result.success(user);
        }else {
            return Result.fail("请求用户详细操作失败");
        }
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/info/{id}")
    public Result getUserById(@PathVariable("id") Long id){
        User user = userService.getById(id);
        List<Role> roles = roleService.listRolesByUserId(id);
        user.setRoles(roles);
        if (user!=null){
            return Result.success(user);
        }else {
            return Result.fail("请求用户详细操作失败");
        }
    }
    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("/update")
    public Result update(@RequestBody User user){
        user.setUpdated(LocalDateTime.now());

        userService.updateById(user);
        return Result.success("");
    }
    @PreAuthorize("hasAuthority('sys:user:save')")
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        user.setStatu(Const.STATUS_ON);
        user.setCreated(LocalDateTime.now());
        //新创建用户密码需要默认密码
        String encode_password = passwordEncoder.encode(Const.DEFAULT_PASSWORD);
        user.setPassword(encode_password);

        if(user.getAvatar()==null || user.getAvatar().equals("")){
//            默认头像
            user.setAvatar(Const.DEFAULT_AVATAR);
        }

        userService.save(user);
        return Result.success(null);
    }
    @Transactional
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        //同步删除其他关联信息
        //ids可能是多个角色编号，不能用eq使用in
        userRoleService.remove(new QueryWrapper<UserRole>().in("user_id",ids));

        //ids中就是要删除的角色
        //mabits-Plus封装的批量删除方法，需要集合类
        userService.removeByIds(Arrays.asList(ids));
        return Result.success("");
    }
    //重置密码
    @PreAuthorize("hasAuthority('sys:user:repass')")
    @PostMapping("/repass")
    public Result repass(@RequestBody Long id){
        User user = userService.getById(id);
        String encode = passwordEncoder.encode(Const.DEFAULT_PASSWORD);
        user.setPassword(encode);
        //跟新用户
        userService.updateById(user);
        return Result.success(null);
    }
}
