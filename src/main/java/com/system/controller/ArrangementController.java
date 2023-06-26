package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.*;
import com.system.service.ArrangementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-21
 */
@RestController
@RequestMapping("/system/arrangement")
public class ArrangementController extends BaseController {
    @Autowired
    ArrangementService arrangementService;

    @PostAuthorize("hasAnyAuthority('sys:arrangement:list')")
    @RequestMapping("/list")
    public Result list(String name, String startTime, String endTime,String size,String current){
        int mCurrent=1;
        int mSize=5;
        if(current!=null){
            mCurrent=Integer.valueOf(current);
        }
        if(size!=null){
            mSize=Integer.valueOf(size);
        }

        QueryWrapper<Arrangement> qw = new QueryWrapper<>();
        if(startTime!=null){
            java.sql.Timestamp start = new java.sql.Timestamp(Long.valueOf(startTime));
            qw.gt("date",start);
//            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//            System.out.println(start.toString());
        }
        if(endTime!=null) {
            java.sql.Timestamp end = new java.sql.Timestamp(Long.valueOf(endTime));
            qw.lt("date",end);
        }
        Page<Arrangement> arrangementPage = arrangementService.page(getPage(),qw.like(StrUtil.isNotBlank(name),"name",name));
//        Page<Arrangement> arrangementPage = arrangementService.page(getPage(),qw);
        return Result.success(arrangementPage);
    }

    //查询---编辑时用
    @PostAuthorize("hasAnyAuthority('sys:arrangement:list')")
    @GetMapping("/info/{id}")
    public Result list(@PathVariable("id") Long id){
        Arrangement arrangement = arrangementService.getById(id);
        return Result.success(arrangement);
    }



    @PostAuthorize("hasAnyAuthority('sys:arrangment:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        arrangementService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }
}
