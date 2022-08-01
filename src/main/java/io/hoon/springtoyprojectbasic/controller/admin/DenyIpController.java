package io.hoon.springtoyprojectbasic.controller.admin;

import io.hoon.springtoyprojectbasic.domain.dto.DenyIpDto;
import io.hoon.springtoyprojectbasic.domain.entity.DenyIp;
import io.hoon.springtoyprojectbasic.security.vote.DenyIpVoter;
import io.hoon.springtoyprojectbasic.service.security.DenyIpService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin/denyIp")
@Controller
public class DenyIpController {

    private final DenyIpService denyIpService;

    private final DenyIpVoter denyIpVoter;

    @GetMapping
    public String getDenyIpList(Model model) {

        List<DenyIp> denyIpList = denyIpService.getDenyIpList();
        model.addAttribute("denyIpList", denyIpList);

        return "admin/ip/list";
    }

    @GetMapping("/{id}")
    public String getDenyIp(@PathVariable Long id,
                         Model model) {


        DenyIp denyIp = denyIpService.getDenyIp(id);

        ModelMapper modelMapper = new ModelMapper();
        DenyIpDto denyIpDto = modelMapper.map(denyIp, DenyIpDto.class);
        model.addAttribute("denyIp", denyIpDto);

        return "admin/ip/detail";
    }

    @PostMapping
    public String createDenyIp(@ModelAttribute("denyIp") DenyIpDto denyIpDto) {

        ModelMapper modelMapper = new ModelMapper();
        DenyIp denyIp = modelMapper.map(denyIpDto, DenyIp.class);
        denyIpService.createDenyIp(denyIp);
        denyIpVoter.reload();
        return "redirect:/admin/denyIp";
    }

    @GetMapping("/register")
    public String register(Model model) {

        DenyIpDto denyIpDto = new DenyIpDto();
        model.addAttribute("denyIp", denyIpDto);

        return "admin/ip/detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteDenyIp(@PathVariable("id") Long id) {
        denyIpService.deleteDenyIp(id);
        return "redirect:/admin/denyIp";
    }

}
