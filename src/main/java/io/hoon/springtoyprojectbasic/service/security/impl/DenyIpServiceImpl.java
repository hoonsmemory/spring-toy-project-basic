package io.hoon.springtoyprojectbasic.service.security.impl;

import io.hoon.springtoyprojectbasic.domain.entity.DenyIp;
import io.hoon.springtoyprojectbasic.repository.DenyIpRepository;
import io.hoon.springtoyprojectbasic.service.security.DenyIpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DenyIpServiceImpl implements DenyIpService {

    private final DenyIpRepository denyIpRepository;

    @Override
    public List<DenyIp> getAccessIp() {

        List<DenyIp> denyIpList = denyIpRepository.findAll();

        return denyIpList;
    }
}
