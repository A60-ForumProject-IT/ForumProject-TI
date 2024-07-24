package com.project;

import com.project.exceptions.*;
import com.project.helpers.PermissionHelper;
import com.project.helpers.TestHelpers;
import com.project.models.FilteredUsersOptions;
import com.project.models.PhoneNumber;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.contracts.PhoneRepository;
import com.project.repositories.contracts.RoleRepository;
import com.project.repositories.contracts.UserRepository;
import com.project.services.PhoneServiceImpl;
import com.project.services.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    UserRepository userRepository;

    @Mock
    PhoneRepository phoneRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    PhoneServiceImpl phoneService;

    @InjectMocks
    UserServiceImpl userService;


    @Test
    public void updateUser_Should_UpdateUser_When_TheSameUserTriesToUpdateTheirProfile() {
        User userToBeUpdated = TestHelpers.createMockNoAdminUser();
        userToBeUpdated.setId(1);
        userToBeUpdated.setUsername("user1");

        User user = TestHelpers.createMockNoAdminUser();
        user.setId(1);

        Mockito.when(userRepository.getByUsername(userToBeUpdated.getUsername()))
                .thenReturn(userToBeUpdated);

        userService.update(user, userToBeUpdated);

        Mockito.verify(userRepository, Mockito.times(1)).update(userToBeUpdated);
    }

    @Test
    public void updateUser_Should_Throw_When_UnauthorizedUserTriesToUpdate() {
        User userToBeUpdated = TestHelpers.createMockNoAdminUser();
        User userThatCannotUpdate = TestHelpers.createMockNoAdminUser();
        userThatCannotUpdate.setId(100);

        Assertions.assertThrows(
                UnauthorizedOperationException.class,
                () -> userService.update(userToBeUpdated, userThatCannotUpdate));
    }

    @Test
    public void getUserByUsername_Should_ReturnUser_When_EverythingIsOk() {
        String mockUserNameByWhichWeSearchUser = "Mock_NoAdmin_User";

        Mockito.when(userRepository.getByUsername(mockUserNameByWhichWeSearchUser))
                .thenReturn(TestHelpers.createMockNoAdminUser());

        userService.getByUsername(mockUserNameByWhichWeSearchUser);

        Mockito.verify(userRepository, Mockito.times(1))
                .getByUsername(mockUserNameByWhichWeSearchUser);
    }


//@Test
//public void makeAdministrativeChanges_Should_Pass_When_TheUserTryingToMakeTheChangesIsAdmin() {
//    User adminUser = TestHelpers.createMockAdminUser();
//    User userToBeUpdated = TestHelpers.createMockNoAdminUser();
//
//    Mockito.when(userRepository.makeAdministrativeChanges(userToBeUpdated))
//            .thenReturn(userToBeUpdated);
//
//    userService.makeAdministrativeChanges(adminUser, userToBeUpdated);
//
//    Mockito.verify(userRepository, Mockito.times(1))
//            .makeAdministrativeChanges(userToBeUpdated);
//}

//@Test
//public void makeAdministrativeChanges_Should_Throw_When_UserTryingToMakeChangesIsNotAdmin() {
//    User nonAdminUser = TestHelpers.createMockNoAdminUser();
//    User userToBeUpdated = TestHelpers.createMockNoAdminUser();
//
//    Assertions.assertThrows(UnauthorizedOperationException.class,
//            () -> userService.makeAdministrativeChanges(nonAdminUser, userToBeUpdated));
//}

    @Test
    public void createUser_Should_CallRepository() {
        User userToBeCreated = TestHelpers.createMockNoAdminUser();

        Mockito.when(userRepository.getByUsername(userToBeCreated.getUsername()))
                .thenThrow(new EntityNotFoundException("User", "username", userToBeCreated.getUsername()));

        Mockito.when(userRepository.getByEmail(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("User", "email", userToBeCreated.getEmail()));

        userService.create(userToBeCreated);

        Mockito.verify(userRepository, Mockito.times(1))
                .create(Mockito.any(User.class));
    }

    @Test
    public void createUser_Should_Throw_When_UsernameExists() {
        User userToBeCreated = TestHelpers.createMockNoAdminUser();

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.create(userToBeCreated));
    }

    @Test
    public void createUser_Should_Throw_When_EmailExists() {
        User existingUser = TestHelpers.createMockNoAdminUser();
        User userToBeCreated = TestHelpers.createMockNoAdminUser();
        existingUser.setUsername("some new username");

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.create(userToBeCreated));
    }

    @Test
    public void getAllUser_Should_Throw_When_UserIsNotAdmin() {
        FilteredUsersOptions filteredUsersOptions = TestHelpers.createUserFilterOptions();
        User nonAdminUser = TestHelpers.createMockNoAdminUser();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.getAllUsers(nonAdminUser, filteredUsersOptions));
    }

    @Test
    public void getAllUsers_Should_Pass_When_Valid() {
        User adminUser = TestHelpers.createMockAdminUser();
        List<User> users = new ArrayList<>();
        users.add(TestHelpers.createMockNoAdminUser());
        FilteredUsersOptions userFilterOptions = TestHelpers.createUserFilterOptions();

        Mockito.when(userRepository.getAllUsers(userFilterOptions))
                .thenReturn(users);

        List<User> allUsers = userService.getAllUsers(adminUser, userFilterOptions);

        Mockito.verify(userRepository, Mockito.times(1))
                .getAllUsers(userFilterOptions);
        Assertions.assertFalse(allUsers.isEmpty());
        Assertions.assertEquals(allUsers.get(0), TestHelpers.createMockNoAdminUser());
    }

    @Test
    public void getAllUsers_Should_Pass_When_ValidWithoutValidAdminUserPassed() {
        User adminUser = TestHelpers.createMockAdminUser();
        List<User> users = new ArrayList<>();
        users.add(TestHelpers.createMockNoAdminUser());
        FilteredUsersOptions filteredUsersOptions = TestHelpers.createUserFilterOptions();

        // Мокиране на поведението на userRepository
        Mockito.when(userRepository.getAllUsers(filteredUsersOptions)).thenReturn(users);

        // Извикване на метода на userService, който трябва да направи повикването към userRepository
        List<User> result = userService.getAllUsers(adminUser, filteredUsersOptions);

        // Проверка дали методът getAllUsers на userRepository е извикан веднъж
        Mockito.verify(userRepository, Mockito.times(1)).getAllUsers(filteredUsersOptions);

        // Проверка дали върнатият резултат е същият като мокирания
        Assertions.assertEquals(users, result);
    }

    @Test
    public void getUserByUsername_Should_CallRepository() {
        User userToFind = TestHelpers.createMockNoAdminUser();

        Mockito.when(userRepository.getByUsername(Mockito.anyString()))
                .thenReturn(userToFind);

        User result = userService.getByUsername(userToFind.getUsername());

        Assertions.assertEquals(userToFind, result);
        Assertions.assertEquals(userToFind.getId(), result.getId());
        Assertions.assertEquals(userToFind.getUsername(), result.getUsername());
        Assertions.assertEquals(userToFind.getEmail(), result.getEmail());
    }

    @Test
    public void addPhoneNumber_Should_Throw_When_UserIsNotAdmin() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPhoneNumber("088913141414");
        User userTryingToAdd = TestHelpers.createMockNoAdminUser();
//    User userToBeUpdated = TestHelpers.createMockNoAdminUser();
//    userToBeUpdated.setId(777);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> phoneService.addPhoneToAnAdmin(userTryingToAdd, phoneNumber));
    }

    //TODO
    @Test
    public void addPhoneNumber_Should_CallRepository_When_UserIsAdmin() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPhoneNumber("088913141414");
        User userToBeUpdated = TestHelpers.createMockAdminUser();

        Mockito.when(phoneRepository.getPhoneNumber(phoneNumber.getPhoneNumber()))
                .thenThrow(new EntityNotFoundException("User", "Phone number", phoneNumber.getPhoneNumber()));

        phoneService.addPhoneToAnAdmin(userToBeUpdated, phoneNumber);

        Mockito.verify(phoneRepository, Mockito.times(1))
                .addPhoneToAdmin(phoneNumber);
    }

    @Test
    public void addPhoneNumber_Should_Throw_When_PhoneNumberExists() {
        User userToBeUpdated = TestHelpers.createMockAdminUser();
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPhoneNumber("088913141414");

        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setPhoneNumber("088913141414");


        Mockito.when(phoneRepository.getPhoneNumber(phoneNumber.getPhoneNumber()))
                .thenReturn(phoneNumber2);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> phoneService.addPhoneToAnAdmin(userToBeUpdated, phoneNumber2));
    }

    @Test
    public void deleteUser_Throw_When_UserIsNotAdminAndNotSame() {
        User user = TestHelpers.createMockNoAdminUser();
        User userWhoDeletes = TestHelpers.createMockNoAdminUser();
        userWhoDeletes.setId(30);

        Assertions
                .assertThrows(UnauthorizedOperationException.class,
                        () -> userService.deleteUser(user, userWhoDeletes.getId()));
    }

    @Test
    public void deleteUser_Should_DeleteUser_When_AdminUserDeletesExistingUser() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToDelete = TestHelpers.createMockNoAdminUser();
        userToDelete.setId(1);

        Mockito.when(userRepository.getUserById(1))
                .thenReturn(userToDelete);

        userService.deleteUser(adminUser, 1);

        Mockito.verify(userRepository, Mockito.times(1)).deleteUser(userToDelete);
    }

    @Test
    public void deleteUser_Should_ThrowEntityNotFound_When_AdminUserDeletesNonExistingUser() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToDelete = TestHelpers.createMockNoAdminUser();

        Mockito.when(userRepository.getUserById(userToDelete.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser(adminUser, userToDelete.getId()));

//        userService.deleteUser(adminUser, userToDelete.getId());
//
//        Mockito.verify(userRepository, Mockito.times(1)).deleteUser(userToDelete);
    }

    @Test
    public void getUsersById_Should_ReturnUser_When_MethodCalled() {
        User user = TestHelpers.createMockNoAdminUser();
        User admin = TestHelpers.createMockAdminUser();

        Mockito.when(userRepository.getUserById(2))
                .thenReturn(user);

        User result = userService.getUserById(admin, user.getId());

        Mockito.verify(userRepository, Mockito.times(1)).getUserById(2);

        Assertions.assertEquals(user, result);
    }


    @Test
    public void getAllUsersCount_Should_Pass() {
        Mockito.when(userRepository.countAllUsers())
                .thenReturn(1L);

        userService.countAllUsers();

        Mockito.verify(userRepository, Mockito.times(1))
                .countAllUsers();
    }

    @Test
    public void getUserByEmail_Should_CallRepository() {
        User userToCallMethod = TestHelpers.createMockNoAdminUser();

        userRepository.getByEmail(userToCallMethod.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .getByEmail(userToCallMethod.getEmail());
    }

 @Test
    public void blockUser_Should_BlockUser_When_AdminUserBlocksExistingUser() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToBlock = TestHelpers.createMockNoAdminUser();
        userToBlock.setBlocked(false);

        Mockito.when(userRepository.getUserById(userToBlock.getId()))
                .thenReturn(userToBlock);

        userService.blockUser(adminUser, userToBlock.getId());
        Mockito.verify(userRepository, Mockito.times(1)).blockUser(userToBlock);

    }

    @Test
    public void blockUser_Should_ThrowBlockedException_When_UserIsAlreadyBlocked() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToBlock = TestHelpers.createMockNoAdminUser();
        userToBlock.setBlocked(true);

        Mockito.when(userRepository.getUserById(userToBlock.getId()))
                .thenReturn(userToBlock);

        Assertions.assertThrows(BlockedException.class, () -> {
            userService.blockUser(adminUser, userToBlock.getId());
        });
    }

    @Test
    public void blockUser_Should_ThrowEntityNotFound_When_UserIsAlreadyBlocked() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToBlock = TestHelpers.createMockNoAdminUser();
        userToBlock.setBlocked(false);

        Mockito.when(userRepository.getUserById(userToBlock.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.blockUser(adminUser, userToBlock.getId());
        });
    }

    @Test
    public void unblockUser_Should_UnBlockUser_When_AdminUserBlocksExistingUser() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToUnblock = TestHelpers.createMockNoAdminUser();
        userToUnblock.setBlocked(true);

        Mockito.when(userRepository.getUserById(userToUnblock.getId()))
                .thenReturn(userToUnblock);

        userService.unblocked(adminUser, userToUnblock.getId());
        Mockito.verify(userRepository, Mockito.times(1)).unblocked(userToUnblock);
    }

    @Test
    public void unBlockUser_Should_ThrowUnBlockedException_When_UserIsAlreadyUnBlocked() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToUnblock = TestHelpers.createMockNoAdminUser();
        userToUnblock.setBlocked(false);

        Mockito.when(userRepository.getUserById(userToUnblock.getId()))
                .thenReturn(userToUnblock);

        Assertions.assertThrows(UnblockedException.class, () -> {
            userService.unblocked(adminUser, userToUnblock.getId());
        });
    }

    @Test
    public void unblockUser_Should_ThrowEntityNotFound_When_UserIsNotExisting() {
        User adminUser = TestHelpers.createMockAdminUser();
        User userToUnblock = TestHelpers.createMockNoAdminUser();
        userToUnblock.setBlocked(false);

        Mockito.when(userRepository.getUserById(userToUnblock.getId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.unblocked(adminUser, userToUnblock.getId());
        });
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void makeAdmin_Should_MakeUserAdmin_When_AdminMakeUserAdmin(){
        User userToBeAdmin = TestHelpers.createMockNoAdminUser();

        userToBeAdmin.setRole(TestHelpers.createAdminRole());

        Mockito.when(userRepository.getUserById(userToBeAdmin.getId()))
                .thenReturn(userToBeAdmin);

        userService.userToBeAdmin(userToBeAdmin);
        Mockito.verify(userRepository, Mockito.times(1)).userToBeModerator(userToBeAdmin);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void makeAdmin_Should_ThrowBlockException_When_AdminMakeUserAdmin(){
        User admin = TestHelpers.createMockAdminUser();
        User userToBeAdmin = TestHelpers.createMockNoAdminUser();
        userToBeAdmin.setBlocked(true);

        userToBeAdmin.setRole(TestHelpers.createAdminRole());

        Mockito.when(userRepository.getUserById(userToBeAdmin.getId()))
                .thenThrow(BlockedException.class);

        userService.userToBeAdmin(userToBeAdmin);
        Mockito.verify(userRepository, Mockito.times(1)).userToBeModerator(userToBeAdmin);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void makeAdmin_Should_ThrowUnauthorized_When_AdminMakeUserAdmin(){
        User admin = TestHelpers.createMockAdminUser();
        User userToBeAdmin = TestHelpers.createMockNoAdminUser();

        Mockito.when(userRepository.getUserById(userToBeAdmin.getId()))
                .thenThrow(UnauthorizedOperationException.class);

        userService.userToBeAdmin(userToBeAdmin);
        Mockito.verify(userRepository, Mockito.times(1)).userToBeModerator(userToBeAdmin);
    }

}