package com.hospital.assistant.account.repo;

import com.hospital.assistant.model.Account;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AccountRepoInMemory implements AccountRepository {
  private Set<Account> accounts;

  public AccountRepoInMemory(Collection<Account> initialAccounts) {
    this.accounts = new HashSet<>(initialAccounts);
  }


  @Override
  public Collection<Account> getAccounts() {
    return accounts;
  }

  @Override
  public void registerAccount(Account account) {
    if (accounts.stream().anyMatch(registeredAccount -> registeredAccount.getName().equals(account.getName()))) {
      throw new IllegalArgumentException(String.format("Account with username %s already exists", account.getName()));
    }
    accounts.add(account);
  }
}
