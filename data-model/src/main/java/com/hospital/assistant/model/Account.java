package com.hospital.assistant.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {
  private String id;
  private String name;
  private String passwordHash;
  private Role role;
}
