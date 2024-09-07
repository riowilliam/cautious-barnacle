package com.fision.dto;

import org.springframework.http.HttpStatus;

/**
 * @author LordDev
 */
public class ResponseDto<T>{
    private String info;
    private T data;
    private int status;

    public ResponseDto(String info, T data, HttpStatus status) {
        this.info = info;
        this.data = data;
        this.status = status.value();
    }

    public ResponseDto(String info, HttpStatus status) {
        this.info = info;
        this.status = status.value();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
