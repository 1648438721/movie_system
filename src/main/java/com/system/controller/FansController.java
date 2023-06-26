package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Arrangement;
import com.system.entity.Category;
import com.system.entity.Fans;
import com.system.entity.User;
import com.system.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-22
 */
@RestController
@RequestMapping("/system/fans")
public class FansController extends BaseController {
    @Autowired
    FansService fansService;

    @Autowired
    PasswordEncoder passwordEncoder;

    //获得所有类别的数据，分页显示
    @PostAuthorize("hasAnyAuthority('sys:fans:list')")
    @RequestMapping("/list")
    public Result list(String name){
        Page<Fans> fansPage = fansService.page(getPage(),new QueryWrapper<Fans>().like(StrUtil.isNotBlank(name), "username",name));
        return Result.success(fansPage);
    }

    //查询---编辑时用
    @PostAuthorize("hasAnyAuthority('sys:fans:list')")
    @GetMapping("/info/{id}")
    public Result list(@PathVariable("id") Long id){
        Fans fans = fansService.getById(id);
        return Result.success(fans);
    }

    //修改密码
    @PostAuthorize("hasAnyAuthority('sys:fans:setpass')")
    @PostMapping("/repass")
    public Result repass(@RequestBody Long id){
        //使用id查询用户详细信息
        Fans fans = fansService.getById(id);
        String encode_password=passwordEncoder.encode(Const.DEFAULT_PASSWORD);
        fans.setPassword(encode_password);
        //更新用户对象
        fansService.updateById(fans);
        return Result.success(null);
    }

    @PostAuthorize("hasAnyAuthority('sys:fans:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        fansService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }
    @GetMapping("/num")
    public Result num(){
        int fans = fansService.count();
        return Result.success(fans);
    }
}
