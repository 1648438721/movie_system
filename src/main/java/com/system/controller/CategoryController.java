package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Category;
import com.system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-23
 */
@RestController
@RequestMapping("/system/category")
public class CategoryController extends BaseController {
    @Autowired
    CategoryService categoryService;

    //获得所有类别的数据，分页显示
    @PreAuthorize("hasAuthority('sys:category:list')")
    @GetMapping("/list")
    public Result list(String name){
        Page<Category> categorys = categoryService.page(getPage(), new QueryWrapper<Category>().like(StrUtil.isNotBlank(name), "name", name));

        return Result.success(categorys);
    }
    //获得所有类别的数据，分页显示
    @PreAuthorize("hasAuthority('sys:category:list')")
    @GetMapping("/info/{id}")
    public Result list(@PathVariable("id") Long id){
        Category category = categoryService.getById(id);
        return Result.success(category);
    }
    //添加类别信息，保存
    @PreAuthorize("hasAuthority('sys:category:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Category category){
        category.setCreated(LocalDateTime.now());
        category.setStatu(Const.STATUS_ON);
        categoryService.save(category);
        return Result.success(null);
    }
    //保存类别信息，跟新
    @PreAuthorize("hasAuthority('sys:category:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Category category){
        category.setUpdated(LocalDateTime.now());
        categoryService.updateById(category);
        return Result.success(null);
    }
    //删除类别信息，删除
    @PreAuthorize("hasAuthority('sys:category:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        categoryService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }
}
