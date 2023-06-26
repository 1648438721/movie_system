package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.common.BaseController;
import com.system.common.Result;
import com.system.entity.Arrangement;
import com.system.entity.Fans;
import com.system.entity.Orders;
import com.system.entity.vo.ChartVo;
import com.system.service.ArrangementService;
import com.system.service.FansService;
import com.system.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/home")
public class HomeController extends BaseController {
    @Autowired
    OrdersService ordersService;
    @Autowired
    FansService fansService;
    @Autowired
    ArrangementService arrangementService;
    //    SELECT sum(price) as money,DATE_FORMAT(pay_at,'%Y-%m-%d') AS data from sys_orders GROUP BY DATE_FORMAT(pay_at,'%Y-%m-%d')
    @GetMapping("/dayMoney")
    public Result dayMoney(){
        List<ChartVo> orderList = new ArrayList<>();
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<Orders>();
        queryWrapper.groupBy("DATE_FORMAT(pay_at,'%Y-%m-%d')");
        queryWrapper.select("sum(price) as money","DATE_FORMAT(pay_at,'%Y-%m-%d') as date");
        queryWrapper.orderByAsc("date");

        List<Orders> orders = ordersService.list(queryWrapper);
        orders.forEach(o->{
            ChartVo order = new ChartVo();
            order.setDate(o.getDate());
            order.setValue(o.getMoney());
            orderList.add(order);
        });

        return Result.success(orderList);
    }

    @GetMapping("/register")
    public Result register(){
        List<ChartVo> fanList = new ArrayList<>();
        QueryWrapper<Fans> queryWrapper = new QueryWrapper<Fans>();
        queryWrapper.groupBy("DATE_FORMAT(created,'%Y-%m-%d')");
        queryWrapper.select("count(id) as people","DATE_FORMAT(created,'%Y-%m-%d') as date");
        queryWrapper.orderByAsc("date");
        List<Fans> fans = fansService.list(queryWrapper);
        fans.forEach(f->{
            ChartVo fan = new ChartVo();
            fan.setDate(f.getDate());
            fan.setValue(f.getPeople());
            fanList.add(fan);
        });

        return Result.success(fanList);
    }

    @GetMapping("/arrangement")
    public Result arrangement(){
        List<ChartVo> arrangementList = new ArrayList<>();
        QueryWrapper<Arrangement> queryWrapper = new QueryWrapper<Arrangement>();
        queryWrapper.groupBy("DATE_FORMAT(date,'%Y-%m-%d')");
        queryWrapper.select("count(id) as movies","DATE_FORMAT(date,'%Y-%m-%d') as time");
        queryWrapper.orderByAsc("time");
        List<Arrangement> arrangements = arrangementService.list(queryWrapper);
        arrangements.forEach(a->{
            ChartVo arrangement = new ChartVo();
            arrangement.setDate(a.getTime());
            arrangement.setValue(a.getMovies());
            arrangementList.add(arrangement);
        });

        return Result.success(arrangementList);
    }
}
