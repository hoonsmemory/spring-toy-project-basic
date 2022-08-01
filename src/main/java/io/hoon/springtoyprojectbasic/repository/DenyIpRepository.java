package io.hoon.springtoyprojectbasic.repository;

import io.hoon.springtoyprojectbasic.domain.entity.DenyIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DenyIpRepository extends JpaRepository<DenyIp, Long> {

    DenyIp findByIpAddress(String IpAddress);
}
