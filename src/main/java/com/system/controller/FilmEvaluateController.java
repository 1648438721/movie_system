package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Film;
import com.system.entity.FilmEvaluate;
import com.system.service.FilmEvaluateService;
import com.system.service.FilmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-23
 */
@Slf4j
@RestController
@RequestMapping("/system/evaluate")
public class FilmEvaluateController extends BaseController {
    @Autowired
    FilmEvaluateService filmEvaluateService;
    @Autowired
    FilmService filmService;

    //获得所有评论的数据，分页显示
    @PreAuthorize("hasAuthority('sys:evaluate:list')")
    @GetMapping("/list")
    public Result list(String uid,String filmname,String comment){
        QueryWrapper<FilmEvaluate> queryWrapper = new QueryWrapper<FilmEvaluate>();
        Long id = null;
        //        SELECT * from sys_film_evaluate WHERE fid = (SELECT id FROM sys_film WHERE name LIKE "%熊猫%")

//        queryWrapper.eq(StrUtil.isNotBlank(filmname),"fid","SELECT id FROM sys_film WHERE name LIKE"+"%"+filmname+"%");
        if (StrUtil.isNotBlank(filmname)) {
            Film film = filmService.getOne(new QueryWrapper<Film>().like("name", filmname));
            id = film.getId();
        }
        Page<FilmEvaluate> evaluates = filmEvaluateService.page(getPage(),
                queryWrapper
                        .like(StrUtil.isNotBlank(uid), "uid", uid)
                        .like(StrUtil.isNotBlank(comment), "comment", comment)
                        .eq(StrUtil.isNotBlank(filmname),"fid",id));


        //根据fid查找电影名称
        evaluates.getRecords().stream().forEach(e->{
            Film film = filmService.getById(e.getFid());
            e.setFname(film.getName());
        });
        return Result.success(evaluates);
    }
    //删除类别信息，删除
    @PreAuthorize("hasAuthority('sys:evaluate:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        filmEvaluateService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }
    //更改状态
    @PreAuthorize("hasAuthority('sys:evaluate:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Long[] ids){
        List<FilmEvaluate> Evaluates = filmEvaluateService.listByIds(Arrays.asList(ids));
        Evaluates.forEach(e->{
            e.setStatu(1-e.getStatu());
            filmEvaluateService.updateById(e);
        });

        return Result.success(null);
    }
}
