package com.XiangQi.XiangQiBE.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerLoginDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
