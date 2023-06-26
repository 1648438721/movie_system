package com.system.config;

import com.system.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//开启方法级权限验证
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Autowired
    CaptchaFilter captchaFilter;

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    JWTAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    JWTLogoutSuccessHandler jwtLogoutSuccessHandler;
    @Bean
    public BCryptPasswordEncoder cryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JWTAuthenticationFilter(authenticationManager());
    }

    //验证端所有的请求路径：都要经过springSecurity的权限验证，但有一些基本路径不能拦截：登录页面包括请求验证码
    //定义白名单 请求的路径只要是白名单列表中的路径 springSecurity放行
    public static final String[] URL_WHITE_LIST = {
            "/login",
            "/captcha",
            "/logout"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭csrf()和cors()
        http.cors().and().csrf().disable()
                //登录的配置
                .formLogin()
                .failureHandler(loginFailureHandler)//配置失败处理器对象
                .successHandler(loginSuccessHandler)//配置登录成功处理器对象

                //配置登出成功处理器对象
                .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)

                .and()//SpringSecurity拦截的规则
                .authorizeRequests()//对所有请求进行SpringSecurity验证， antMatchers(URL_WHITE_LIST)请求放行的规则，使用白名单
                .antMatchers(URL_WHITE_LIST).permitAll() // permitAll()对所有人采用这些规则
                .anyRequest().authenticated()
                //anyRequest() 表示匹配任意的URL请求 authenticated() 任何请求都必须被SpringSecurity验证后才能用
                .and()//因为采用的是前后端分离开发，不使用session技术，禁用 session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                //SessionCreationPolicy.STATELESS SpringSecurity永远不会创建session对象，不会使用HttpSession创建session
                //SessionCreationPolicy.ALWAYS 总是创建HttpSession
                //SessionCreationPolicy.IF_REQUIRED SpringSecurity只会在需要时创建一个HttpSession
                //SessionCreationPolicy.NEVER SpringSecurity不会创建session对象，但是如果已经存在session，是可以使用session的。

                //异常处理器
                .and()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)

                //配置自定义过滤器
                .and() //在SpringSecurity 验证 用户名和密码的过滤器执行之前，执行我们自定义的captchaFilter验证码过滤器
                .addFilter(jwtAuthenticationFilter())
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
