package com.hospital.assistant.api.dto;

import com.hospital.assistant.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAccountDto {
  private String username;
  private String password;
  private Role role;
}
