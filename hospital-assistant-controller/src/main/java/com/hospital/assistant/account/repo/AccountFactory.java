package com.hospital.assistant.account.repo;

import com.hospital.assistant.model.Account;
import com.hospital.assistant.model.Role;

public interface AccountFactory {
  Account createInstance(String username, String password, Role role);
}
