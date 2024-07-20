package com.project.services.contracts;

import com.project.models.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();

    Role getRoleById(int id);
}
