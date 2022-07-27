package io.hoon.springtoyprojectbasic.repository;

import io.hoon.springtoyprojectbasic.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {
}
