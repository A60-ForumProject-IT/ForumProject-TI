package com.project.services;

import com.project.models.Role;
import com.project.repositories.contracts.RoleRepository;
import com.project.services.contracts.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository  = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.getAllRoles();
    }

    @Override
    public Role getRoleById(int id) {
        return roleRepository.getRoleById(id);
    }
}
