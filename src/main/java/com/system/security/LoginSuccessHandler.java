package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import com.system.entity.User;
import com.system.service.UserService;
import com.system.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;Charset=utf-8");
        //产生一个令牌token，将token响应回vue端。
        ServletOutputStream out = response.getOutputStream();

        String username = authentication.getName();//获得验证登录成功的用户名，之前会进行输入用户名和密码的正确验证。


        String token = jwtUtil.createToken(username);
//响应token的同时，将登录用户成功的信息一起响应。需要使用username查询到具体的用户信息：
//将token挂到 响应头："token":"23232323232323.2323232323232323.sda3ewewewew"
        response.setHeader(jwtUtil.getHeader(),token);
        User user = userService.getUserByUsername(username);
        Result result = Result.success(user);
//登录校验成功，处理器类：返回一个成功的 Result对象。
        out.write(JSONUtil.toJsonStr(result).getBytes("utf-8"));
        out.flush();
        out.close();
    }
}
