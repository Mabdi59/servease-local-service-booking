package com.servease.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

   private int userId;  // Changed from id to userId to match DB

   @NotEmpty(message = "Username cannot be empty.")
   private String username;

   @NotEmpty(message = "Full name cannot be empty.")
   private String fullName;

   @Email(message = "Invalid email format.")
   @NotEmpty(message = "Email is required.")
   private String email;

   @JsonIgnore
   @NotEmpty(message = "Password cannot be empty.")
   private String passwordHash;

   private String phone;

   @JsonIgnore
   private boolean activated;

   @NotEmpty(message = "Role cannot be empty.")
   private String role;

   private Set<Authority> authorities = new HashSet<>();

   public User(int userId, String username, String fullName, String email, String passwordHash, String phone, String role) {
      this.userId = userId;
      this.username = username;
      this.fullName = fullName;
      this.email = email;
      this.passwordHash = passwordHash;
      this.phone = phone;
      this.role = role;
      this.activated = true;
      this.setAuthorities(role);
   }

   public void setAuthorities(String roles) {
      this.authorities.clear();
      String[] roleArray = roles.split(",");
      for (String role : roleArray) {
         String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
         this.authorities.add(new Authority(formattedRole));
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return userId == user.userId &&
              activated == user.activated &&
              Objects.equals(username, user.username) &&
              Objects.equals(fullName, user.fullName) &&
              Objects.equals(email, user.email) &&
              Objects.equals(phone, user.phone) &&
              Objects.equals(passwordHash, user.passwordHash) &&
              Objects.equals(role, user.role) &&
              Objects.equals(authorities, user.authorities);
   }

   @Override
   public int hashCode() {
      return Objects.hash(userId, username, fullName, email, phone, passwordHash, activated, role, authorities);
   }
}
