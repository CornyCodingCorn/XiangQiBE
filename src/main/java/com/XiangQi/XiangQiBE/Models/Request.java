package com.XiangQi.XiangQiBE.Models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Request {
    public enum Type {
        EMAIL_VERIFY,
        CHANGE_PASSWORD,
    }

    @Id
    private String id;
    
    @NotBlank
    @lombok.NonNull
    @Size(max=20)
    private String username;

    @NotBlank
    private Type type;
}
