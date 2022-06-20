package com.XiangQi.XiangQiBE.Models;

import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Document("RefreshTokens")
@Getter
@RequiredArgsConstructor
public class Token {
    @Id
    private String id;

    @NotBlank
    @Setter
    @NonNull
    private String token;

    @NotBlank
    @Setter
    private String username;
}
