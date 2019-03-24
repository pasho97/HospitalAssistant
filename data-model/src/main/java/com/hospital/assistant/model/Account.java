package com.hospital.assistant.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {
  private String id;
  private String name;
  private String passwordHash;
  private Role role;
  private FirebaseData firebaseData;
  private List<IntervalDto> interval = new ArrayList<>();
}
