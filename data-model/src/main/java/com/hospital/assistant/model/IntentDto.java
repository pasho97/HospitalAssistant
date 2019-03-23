package com.hospital.assistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntentDto {
  private String message;
  private Role contactRole;
  private Priority priority;
}
