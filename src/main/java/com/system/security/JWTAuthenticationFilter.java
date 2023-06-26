package com.system.security;

import cn.hutool.core.util.StrUtil;
import com.system.entity.User;
import com.system.service.UserService;
import com.system.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeSet;

@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        验证jwt代码
        log.info("JWT校验过滤器执行");
        //获得vue端提交 请求头中token
        String jwt = request.getHeader("token");
        //判断是不是空 是不是Undefined
        if (StrUtil.isBlankOrUndefined(jwt)){
            chain.doFilter(request,response);
            return;
        }
        Claims claims = jwtUtil.getClaimsByToken(jwt);
        if (claims==null){
            throw new JwtException("token解析异常");
        }
        if (jwtUtil.isTokenExpired(claims)){
            throw new JwtException("token已经过期");
        }
        //获得用户信息： jwt的token挂载 信息（用户名）。 claims得的创建token时存入用户名
        String username = claims.getSubject();
        log.info("用户{}-----正在访问系统",username);
        User user = userService.getUserByUsername(username);
        //参数1：用户名,参数2，null，参数3，需要使用user查询到该用户的详细权限信息
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,null,userDetailsService.getUserAuthority(user.getId()));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request,response);
    }
}
