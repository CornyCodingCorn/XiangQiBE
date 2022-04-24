package com.XiangQi.XiangQiBE.Models;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject<TData> {
    public HttpStatus status;
    public String message;
    public TData data;

    public static <TData> ResponseEntity<ResponseObject<TData>> Response(HttpStatus status, String message, TData data) {
        return ResponseEntity.status(status).body(new ResponseObject<TData>(status, message, data));
    }
}
