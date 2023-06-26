package com.system.common;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.service.MenuService;
import com.system.service.RoleService;
import com.system.service.UserService;
import com.system.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Autowired
    protected HttpServletRequest request;
//项目中 控制器需要哪些 注入对象
    @Autowired
    protected UserService userService;

    @Autowired
    protected RedisUtil redisUtil;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected RoleService roleService;

    public Page getPage(){
        //获得请求当前页，默认第1页
        int current = ServletRequestUtils.getIntParameter(request, "current", 1);
        //一夜显示几条，默认10条
        int size = ServletRequestUtils.getIntParameter(request, "size", 5);
        return new Page(current,size);
    }
}