package io.hoon.springtoyprojectbasic.controller.admin;

import io.hoon.springtoyprojectbasic.domain.dto.ResourceDto;
import io.hoon.springtoyprojectbasic.domain.entity.Resource;
import io.hoon.springtoyprojectbasic.domain.entity.Role;
import io.hoon.springtoyprojectbasic.repository.RoleRepository;
import io.hoon.springtoyprojectbasic.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.hoon.springtoyprojectbasic.service.security.ResourceService;
import io.hoon.springtoyprojectbasic.service.security.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class ResourceController {

    private final ResourceService resourceService;

    private final RoleRepository roleRepository;

    private final RoleService roleService;

    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @GetMapping(value="/resources")
    public String getResources(Model model) {

        List<Resource> resources = resourceService.getResource();
        model.addAttribute("resources", resources);

        return "admin/resource/list";
    }

    @PostMapping(value="/resources")
    public String createResources(ResourceDto resourceDto) {

        ModelMapper modelMapper = new ModelMapper();
        Role role = roleRepository.findByRoleName(resourceDto.getRoleName());
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        Resource resource = modelMapper.map(resourceDto, Resource.class);
        resource.setRoleSet(roles);

        resourceService.createResource(resource);
        urlFilterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }

    @GetMapping(value="/resources/register")
    public String viewRoles(Model model) {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);

        ResourceDto resources = new ResourceDto();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role());
        resources.setRoleSet(roleSet);
        model.addAttribute("resources", resources);

        return "admin/resource/detail";
    }

    @GetMapping(value="/resources/{id}")
    public String getResources(@PathVariable String id, Model model) {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
        Resource resources = resourceService.getResource(Long.valueOf(id));

        ModelMapper modelMapper = new ModelMapper();
        ResourceDto resourcesDto = modelMapper.map(resources, ResourceDto.class);
        model.addAttribute("resources", resourcesDto);

        return "admin/resource/detail";
    }

    @GetMapping(value="/resources/delete/{id}")
    public String removeResources(@PathVariable String id, Model model) {

        Resource resources = resourceService.getResource(Long.valueOf(id));
        resourceService.deleteResource(Long.valueOf(id));
        urlFilterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }
}
