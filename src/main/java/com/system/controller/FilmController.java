package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Category;
import com.system.entity.Film;
import com.system.service.FilmService;
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
 * @since 2023-05-28
 */
@RestController
@RequestMapping("/system/film")
public class FilmController extends BaseController {
    @Autowired
    FilmService filmService;

    //获得所有电影的数据，分页显示
    @PreAuthorize("hasAuthority('sys:film:list')")
    @GetMapping("/list")
    public Result list(String name){

        Page<Film> films = filmService.page(getPage(),new QueryWrapper<Film>().like(StrUtil.isNotBlank(name),"name",name));

        return Result.success(films);
    }

    //通过类别id，查询电影的详细的信息
    @PreAuthorize("hasAuthority('sys:film:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        Film film = filmService.getById(id);
        return Result.success(film);
    }

    //添加电影信息，保存
    @PreAuthorize("hasAuthority('sys:film:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Film film){
        film.setCreated(LocalDateTime.now());
        film.setStatu(Const.STATUS_ON);
        filmService.save(film);
        return Result.success(null);
    }

    //更新电影信息，保存
    @PreAuthorize("hasAuthority('sys:film:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Film film){
        film.setUpdated(LocalDateTime.now());
        filmService.updateById(film);
        return Result.success(null);
    }

    //删除电影信息，删除
    @PreAuthorize("hasAuthority('sys:film:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        filmService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }

    @GetMapping("/num")
    public Result num(){
        int films = filmService.count();
        return Result.success(films);
    }
}
