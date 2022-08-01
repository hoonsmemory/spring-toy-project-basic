package io.hoon.springtoyprojectbasic.service.security;

import io.hoon.springtoyprojectbasic.domain.entity.DenyIp;

import java.util.List;

public interface DenyIpService {

    List<DenyIp> getAccessIp();
}
