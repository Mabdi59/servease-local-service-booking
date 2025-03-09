package com.servease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponseDto {

    @JsonProperty("token")
    private String token;

    @JsonProperty("user")
    private User user;
}
