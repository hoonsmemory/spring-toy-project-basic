package io.hoon.springtoyprojectbasic.repository;

import io.hoon.springtoyprojectbasic.domain.entity.AccessIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessIpRepository  extends JpaRepository<AccessIp, Long> {

    AccessIp findByIpAddress(String IpAddress);
}
