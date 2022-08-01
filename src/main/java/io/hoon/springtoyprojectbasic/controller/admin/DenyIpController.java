package io.hoon.springtoyprojectbasic.controller.admin;

import io.hoon.springtoyprojectbasic.domain.entity.DenyIp;
import io.hoon.springtoyprojectbasic.service.security.DenyIpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class DenyIpController {

    private final DenyIpService denyIpService;

    @GetMapping("/ip")
    public String AccessIpList(Model model) {

        List<DenyIp> denyIp = denyIpService.getAccessIp();
        model.addAttribute("accessIp", denyIp);

        return "admin";
    }

}
