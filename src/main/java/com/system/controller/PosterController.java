package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;

import com.system.entity.Poster;
import com.system.service.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/system/poster")
public class PosterController extends BaseController {
    @Autowired
    PosterService posterService;

    //获得所有电影的数据，分页显示
    @PreAuthorize("hasAuthority('sys:poster:list')")
    @GetMapping("/list")
    public Result list(String name){
        Page<Poster> posters = posterService.page(getPage(),new QueryWrapper<Poster>().like(StrUtil.isNotBlank(name),"name",name));

        return Result.success(posters);
    }

    //通过id，查询电影的详细的信息
    @PreAuthorize("hasAuthority('sys:poster:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        Poster poster = posterService.getById(id);
        return Result.success(poster);
    }

    //添加电影信息，保存
    @PreAuthorize("hasAuthority('sys:poster:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Poster poster){
        poster.setCreated(LocalDateTime.now());
        poster.setStatu(Const.STATUS_ON);
        posterService.save(poster);
        return Result.success(null);
    }

    //更新电影信息，保存
    @PreAuthorize("hasAuthority('sys:poster:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Poster poster){
        poster.setUpdated(LocalDateTime.now());
        posterService.updateById(poster);
        return Result.success(null);
    }

    //删除电影信息，删除
    @PreAuthorize("hasAuthority('sys:poster:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        posterService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }
}
