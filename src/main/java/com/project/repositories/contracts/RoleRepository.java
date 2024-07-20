package com.project.repositories.contracts;

import com.project.models.Role;

import java.util.List;

public interface RoleRepository {
    List<Role> getAllRoles();

    Role getRoleById(int id);
}
