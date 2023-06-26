package com.system.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "token.jwt")
//@ConfigurationProperties 用于读取application.yaml ( .properties )配置文件，获得配置文件中参数值：
//prefix = "token.jwt" 读取的就是application.yaml配置文件中 jwt的参数值
public class JwtUtil {
    private Long expire;
    private String secret;
    private String header;

    //生存token字符串
    public String createToken(String username){
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime()+1000*expire);

        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    //解析jwt --- 登录成功之后，每次对于服务器的访问，都必须解析请求时携带jwt。
    //如果返回有Claims对象，就是解析成功，没有null，就是解析失败.
    public Claims getClaimsByToken(String jwt){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
    }
    //jwt是否过期
    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }
}
