package com.system.common.exception;

import com.system.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.channels.AcceptPendingException;
import java.nio.file.AccessDeniedException;

//全局异常处理
@Slf4j
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = AccessDeniedException.class)
    public Result handler(AccessDeniedException e) throws AccessDeniedException {
//       log.info("spring security权限不足--------------{}",e.getMessage());
//       return Result.fail("访问权限不足");
        throw e;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e){
        log.info("接口参数错误--------------{}",e.getMessage());
        return Result.fail("接口参数错误");
    }

    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e){
        log.info("运行时异常--------------{}",e.getMessage());
        return Result.fail("运行时异常"+e.getMessage());
    }
}
