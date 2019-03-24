package com.hospital.assistant.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntervalDto {
  private LocalDateTime start;
  private LocalDateTime end;
}
