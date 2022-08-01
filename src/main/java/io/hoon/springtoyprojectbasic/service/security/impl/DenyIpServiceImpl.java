package io.hoon.springtoyprojectbasic.service.security.impl;

import io.hoon.springtoyprojectbasic.domain.entity.DenyIp;
import io.hoon.springtoyprojectbasic.repository.DenyIpRepository;
import io.hoon.springtoyprojectbasic.service.security.DenyIpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class DenyIpServiceImpl implements DenyIpService {

    private final DenyIpRepository denyIpRepository;

    @Override
    public List<DenyIp> getDenyIpList() {
        return denyIpRepository.findAll();
    }

    @Override
    public DenyIp getDenyIp(Long id) {
        return denyIpRepository.findById(id).orElse(new DenyIp());
    }

    @Override
    public void createDenyIp(DenyIp denyIp) {
        denyIpRepository.save(denyIp);
    }

    @Override
    public void deleteDenyIp(Long id) {
        denyIpRepository.deleteById(id);
    }
}
