package com.XiangQi.XiangQiBE.Models;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResponseObject<TData> {
    public int status;
    public HttpStatus statusName;
    public String message;
    public TData data;

    public ResponseObject(HttpStatus status, String message, TData data) {
        this.statusName = status;
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public static <TData> ResponseEntity<ResponseObject<TData>> Response(HttpStatus status, String message, TData data) {
        return ResponseEntity.status(status).body(new ResponseObject<TData>(status, message, data));
    }
}
