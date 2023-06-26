package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {
    //权限不足处理器
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = httpServletResponse.getOutputStream();
        log.info("---权限不足---");
        //请求失败参数对象，封装Result
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result result = Result.fail("权限不足"+e.getMessage());
        out.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
