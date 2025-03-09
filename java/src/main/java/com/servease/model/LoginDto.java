package com.servease.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class LoginDto {

   @NotEmpty(message = "Username cannot be empty.")
   private String username;

   @NotEmpty(message = "Password cannot be empty.")
   private String password;
}
