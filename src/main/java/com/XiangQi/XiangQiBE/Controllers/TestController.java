package com.XiangQi.XiangQiBE.Controllers;

import com.XiangQi.XiangQiBE.Models.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1")
public class TestController {
    @GetMapping("/1")
    ResponseEntity<ResponseObject<Object>> testEndpoint() {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Object>(HttpStatus.OK, "test endpoint", null));
    }
}
