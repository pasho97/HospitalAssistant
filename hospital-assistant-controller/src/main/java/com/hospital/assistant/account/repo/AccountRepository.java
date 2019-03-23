package com.hospital.assistant.account.repo;

import com.hospital.assistant.model.Account;
import java.util.Collection;

public interface AccountRepository {

  Collection<Account> getAccounts();

  void registerAccount(Account account);
}
