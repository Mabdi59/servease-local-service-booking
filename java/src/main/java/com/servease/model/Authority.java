package com.servease.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Authority {

   @NotEmpty(message = "Authority name cannot be empty.")
   private String name;
}
