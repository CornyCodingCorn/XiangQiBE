package com.XiangQi.XiangQiBE.Models;

import java.sql.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.internal.util.stereotypes.Immutable;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Match {
    @Id
    @Immutable
    private String id;

    @NotNull
    @NotBlank
    private Date time;

    @NotNull
    @NotBlank
    private String[] moves;

    @NotNull
    @NotBlank
    private String victor;

    @NotNull
    @NotBlank
    private String redPlayer;

    @NotNull
    @NotBlank
    private String blackPlayer;
}
