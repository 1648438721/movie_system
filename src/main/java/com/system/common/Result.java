package com.system.common;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String message;
    private Object resultdata;

    public static Result success(int code,String message,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMessage(message);
        r.setResultdata(data);
        return r;
    }
    public static Result success(Object data){
        return success(20000,"请求操作成功",data);
    }


    public static Result fail(int code,String message,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMessage(message);
        r.setResultdata(data);
        return r;
    }
    public static Result fail(String message){
        return fail(400,message,null);
    }
}
