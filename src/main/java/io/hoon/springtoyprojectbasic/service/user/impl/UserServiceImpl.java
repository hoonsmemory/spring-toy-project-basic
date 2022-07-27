package io.hoon.springtoyprojectbasic.service.user.impl;

import io.hoon.springtoyprojectbasic.domain.Account;
import io.hoon.springtoyprojectbasic.repository.UserRepository;
import io.hoon.springtoyprojectbasic.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void createUser(Account account) {
        userRepository.save(account);
    }
}
