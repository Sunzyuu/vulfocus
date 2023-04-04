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
        return new Result(200, "ok", null);
    }

    public static Result ok(String msg){
        return new Result(200, msg, null);
    }

    public static Result ok(Object data){
        return new Result(200, "ok", data);
    }

    public static Result ok(String msg, Object data){
        return new Result(200, msg, data);
    }



    public static Result fail(String msg){
        return new Result(401, msg, null);
    }

    public static Result fail(){
        return new Result(401, "error", null);
    }

}
