package com.system.security;

import com.system.entity.User;
import com.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserService userService;
    @Override
    public AccountUser loadUserByUsername(String username) throws UsernameNotFoundException {
        //验证用户名密码是否正确
        //使用用户名查询详细信息
        User user = userService.getUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("用户名或密码不存在");
        }
        //返回User对象详细信息
        return new AccountUser(user.getId(),user.getUsername(),user.getPassword(),getUserAuthority(user.getId()));
    }

    //获得权限信息的方法
    public List<GrantedAuthority> getUserAuthority(Long userId){
        String userAuthorityInfo = userService.getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(userAuthorityInfo);

    }
}
