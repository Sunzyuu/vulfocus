package com.sunzy.vulfocus.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Integer status;
    private String msg;
    private Object data;

    public static Result ok(){
        return new Result(SystemConstants.HTTP_OK, "ok", null);
    }

    public static Result ok(String msg){
        return new Result(SystemConstants.HTTP_OK, msg, null);
    }

    public static Result ok(Object data){
        return new Result(SystemConstants.HTTP_OK, "ok", data);
    }

    public static Result ok(String msg, Object data){
        return new Result(SystemConstants.HTTP_OK, msg, data);
    }



    public static Result fail(String msg){
        return new Result(SystemConstants.HTTP_ERROR, msg, null);
    }

    public static Result fail(String msg, Object data){
        return new Result(SystemConstants.HTTP_ERROR, msg, data);
    }
    public static Result fail(){
        return new Result(SystemConstants.HTTP_ERROR, "error", null);
    }


    public static Result build(String msg, Object data){
        return new Result(201, msg, data);
    }

    public static Result running(String msg, Object data){
        return new Result(1001, msg, data);
    }
}
