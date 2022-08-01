package io.hoon.springtoyprojectbasic.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DenyIpDto {

    private Long id;
    private String ipAddress;
}
