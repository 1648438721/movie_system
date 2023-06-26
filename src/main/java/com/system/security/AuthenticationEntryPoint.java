package com.system.security;
import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Component
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//认证失败，执行该方法：
        log.info("认证失败..");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//http 401状态码
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = response.getOutputStream();
        Result result = Result.fail("认证失败，请重新登陆");
        out.write(JSONUtil.toJsonStr(result).getBytes("utf-8"));
        out.flush();
        out.close();
    }
}
