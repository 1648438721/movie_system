package com.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Menu;
import com.system.entity.User;
import com.system.mapper.RoleMapper;
import com.system.mapper.UserMapper;
import com.system.service.MenuService;
import com.system.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sun.misc.BASE64Encoder;

import java.util.List;

@SpringBootTest
class AdminSystemServerApplicationTests {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserService userService;
    @Autowired
    MenuService menuService;
    @Test
    public void testMenuTree(){
        menuService.getCurrentUserNav("admin");
    }
    @Test
    public void permsTest(){
        String s = userService.getUserAuthorityInfo(2L);
        System.out.println(s);
    }

    @Test
    public void testPassword(){
        String pwd = "123";
        String encode = bCryptPasswordEncoder.encode(pwd);
        System.out.println(encode);
        String inputPwd = "123";
        bCryptPasswordEncoder.matches(inputPwd,encode);
    }
    @Test
    public void fun() throws Exception{
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode("JYK".getBytes("utf-8"));
        System.out.println(encode);
    }
    @Test
    public void testSelect(){
        List<User> users = userMapper.selectList(null);
        for(User u : users){
            System.out.println(u);
        }
    }

    @Test
    public void testSelectById(){
        User user = userMapper.selectById(9);
        System.out.println(user);
        user.setUsername("JYKkkkkk");
        user.setPassword("123123");
        user.setId(null);
        int result = userMapper.insert(user);
        System.out.println(result);
    }
    @Test
    public void testUpdate(){
        User user = userMapper.selectById(9);
        user.setUsername("万里学院");
        user.setPassword("123123123");
        int result = userMapper.updateById(user);
        System.out.println(result);
    }
    @Test
    public void testDelete(){
        int i = userMapper.deleteById(9);
        System.out.println(i);
    }

    @Test
    public void testQueryWrapper(){
        QueryWrapper qw = new QueryWrapper();
        qw.gt("id",9);
        qw.lt("id",20);
        qw.isNull("tel");
        List list = userMapper.selectList(qw);
        System.out.println(list);
    }
}
