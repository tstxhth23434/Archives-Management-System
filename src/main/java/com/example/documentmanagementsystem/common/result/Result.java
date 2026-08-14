package com.example.documentmanagementsystem.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    //作用是给这个类加一个“版本标签”，确保序列化和反序列化时的版本一致性。
    private static final long serialVersionUID = 1L;

    /**
     * 状态码: 200 成功, 其他为异常
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;


   public Result() {

   }

   public Result(Integer code, String message, T data){
       this.code = code;
       this.message = message;
       this.data = data;
   }

   public static <T> Result<T> success(){
       return new Result<>(200, "操作成功", null);
   }

   public static <T> Result<T> success(T data){
       return new Result<>(200, "操作成功", data);
   }

    public static <T> Result<T> success(Integer code, String message){
        return new Result<>(code, message, null);
    }

   public static <T> Result<T> success(String message, T data){
       return new Result<>(200, message, data);
   }

   public static <T> Result<T> error(){
       return new Result<>(500, "操作失败", null);
   }

   public static <T> Result<T> error(String message){
       return new Result<>(500, message, null);
   }

   public static <T> Result<T> error(Integer code, String message){
       return new Result<>(code, message, null);
   }

   public static <T> Result<T> error(String message, T data){
       return new Result<>(500, message, data);
   }

}
