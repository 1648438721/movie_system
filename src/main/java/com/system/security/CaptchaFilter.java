package com.system.security;

import com.system.common.exception.CaptchaException;
import com.system.common.lang.Const;
import com.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Component
public class CaptchaFilter extends OncePerRequestFilter {
    private final String loginURL = "/login";
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LoginFailureHandler loginFailureHandler;

    //具体校验验证码方法
    private void validate(HttpServletRequest request) throws CaptchaException{
        String code = request.getParameter("code");//获得前端登录输入的验证码
        String key = request.getParameter("key");

        //commons-Lang3工具类，判断字符对象是不是空的 isBlank
        if(StringUtils.isBlank(code) || StringUtils.isBlank(key)){
            //如果请求验证码或者验证码存储的key有一个是空的，抛出异常给spring security
            //自定义一个异常：验证码失败
            throw new CaptchaException("验证码不能为空");
        }
        log.info("用户输入的验证码{}--redis读取出的验证码{}",code,redisUtil.hget(Const.CAPTACHA_KEY,key));
        if (!code.equals(redisUtil.hget(Const.CAPTACHA_KEY,key))){
            throw new CaptchaException("验证码不正确");
        }
        //后面代码，验证码验证成功，删除验证码，验证码是一次性
        redisUtil.hdel(Const.CAPTACHA_KEY,key);
    }

    //过滤器主要过滤代码的方法
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws IOException,ServletException{
        String uri = request.getRequestURI();
        if (loginURL.equals(uri) && request.getMethod().equals("POST")){
            log.info("login请求链接，正在校验验证码---"+uri);
            //校验验证码
            try {
                this.validate(request);
            } catch (CaptchaException e) {
                log.info(e.getMessage());
                //验证失败：调用登录失败处理器
                loginFailureHandler.onAuthenticationFailure(request,response,e);
            }
        }
        //验证成功过滤器跳转后面资源：验证用户名密码
        filterChain.doFilter(request,response);
    }
}
