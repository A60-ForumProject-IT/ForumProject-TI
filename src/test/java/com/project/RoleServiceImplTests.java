package com.project;

import com.project.helpers.TestHelpers;
import com.project.models.Role;
import com.project.repositories.contracts.RoleRepository;
import com.project.services.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTests {

    @Mock
    RoleRepository roleMockRepository;

    @InjectMocks
    RoleServiceImpl roleMockService;

    @Test
    public void getRoleById_Should_ReturnRole_When_MethodCalled() {
        Mockito.when(roleMockRepository.getRoleById(2))
                .thenReturn(TestHelpers.createUserRole());

        Role role = roleMockService.getRoleById(2);

        Assertions.assertEquals(1, role.getRoleId());
    }

    @Test
    public void getAllRoles_Should_CallRepository() {
        roleMockService.getAllRoles();

        Mockito.verify(roleMockRepository, Mockito.times(1))
                .getAllRoles();
    }
}