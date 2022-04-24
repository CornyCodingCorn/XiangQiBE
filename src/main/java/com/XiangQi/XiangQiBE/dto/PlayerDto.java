package com.XiangQi.XiangQiBE.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerDto {
    @NotBlank
    private String id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
}
