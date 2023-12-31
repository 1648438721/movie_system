package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = httpServletResponse.getOutputStream();
        //请求失败参数对象，封装Result
        Result result = Result.fail("登录验证失败");
        out.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
