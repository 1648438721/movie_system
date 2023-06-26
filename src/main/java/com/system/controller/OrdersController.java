package com.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Arrangement;
import com.system.entity.Orders;
import com.system.service.ArrangementService;
import com.system.service.FansService;
import com.system.service.OrdersService;
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
@RequestMapping("/system/orders")
public class OrdersController extends BaseController {
    @Autowired
    OrdersService ordersService;

    @Autowired
    ArrangementService arrangementService;

    @Autowired
    FansService fansService;

    @GetMapping("/list")
    @PostAuthorize("hasAnyAuthority('sys:order:list')")
    public Result list(String filmName, String startTime, String endTime,String size,String current){
        int mCurrent=1;
        int mSize=5;
        if(current!=null){
            mCurrent=Integer.valueOf(current);
        }
        if(size!=null){
            mSize=Integer.valueOf(size);
        }

        QueryWrapper<Orders> qw = new QueryWrapper<>();
        if(startTime!=null){
            Date start = new Date(Long.valueOf(startTime));
            qw.gt("created",start);
        }
        if(endTime!=null) {
            Date end = new Date(Long.valueOf(endTime));
            qw.lt("created",end);
        }
        Page<Orders> ordersPage = ordersService.page(getPage(),qw);
        //先查询出分页后的结果
        List<Orders> ordersList =  ordersPage.getRecords();
        if(filmName!=null){
            //根据影片名字获得影片对象
            QueryWrapper<Arrangement> arrangementQueryWrapper = new QueryWrapper<>();
            List<Arrangement> arrangementList = arrangementService.list(arrangementQueryWrapper.like("name",filmName));
            List<Orders> newList = ordersService.list();
            List<Orders> oList = new ArrayList<>();
            int a=0;
            for(int i=0;i<newList.size();i++){
                Orders orders = newList.get(i);
                orders.setFansName(fansService.getById(orders.getUid()).getUsername());
                for(int j=0;j<arrangementList.size();j++){
                    if(oList.size()==mSize||oList.size()==ordersList.size()){
                        break;
                    }
                    Arrangement arrangement = arrangementList.get(j);
                    if(orders.getAid()==arrangement.getId()){
                        a++;
                        orders.setFilmName(arrangement.getName());
                        if((mCurrent-1)*mSize<=a){
                            oList.add(orders);
                        }
                    }
                }
            }
            ordersPage.setRecords(oList);
            return Result.success(ordersPage);
        }else {
            for(Orders orders:ordersList){
                orders.setFilmName(arrangementService.getById(orders.getAid()).getName());
                orders.setFansName(fansService.getById(orders.getUid()).getUsername());
            }
            ordersPage.setRecords(ordersList);
            return Result.success(ordersPage);
        }

    }

    @PostAuthorize("hasAnyAuthority('sys:order:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        ordersService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }

    @GetMapping("/num")
    public Result num(){
        int orders = ordersService.count();
        return Result.success(orders);
    }

    @GetMapping("/money")
    public Result money(){

        Orders one = ordersService.getOne(new QueryWrapper<Orders>().select("sum(price) as sumall"));
        return Result.success(one);
    }


}
