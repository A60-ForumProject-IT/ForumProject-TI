package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.PermissionHelper;
import com.project.models.PhoneNumber;
import com.project.models.User;
import com.project.repositories.contracts.PhoneRepository;
import com.project.repositories.contracts.UserRepository;
import com.project.services.contracts.PhoneService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneServiceImpl implements PhoneService {
    private static final String INVALID_PERMISSION = "You dont have permissions! Only admins can do this operation.";
    public static final String DO_NOT_HAVE_PERMISSION_TO_REMOVE_THIS_PHONE_NUMBER = "You do not have permission to remove this phone number.";
    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;

    @Autowired
    public PhoneServiceImpl(PhoneRepository phoneRepository, UserRepository userRepository) {
        this.phoneRepository = phoneRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addPhoneToAnAdmin(User userToAddPhone, PhoneNumber phoneNumber) {
        PermissionHelper.isAdmin(userToAddPhone, INVALID_PERMISSION);
        boolean duplicateExist = true;
        try {
            phoneRepository.getPhoneNumber(phoneNumber.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            duplicateExist = false;
        }
        if (duplicateExist) {
            throw new DuplicateEntityException("Phone", "number", phoneNumber.getPhoneNumber());
        }
        phoneNumber.setUser(userToAddPhone);
        phoneRepository.addPhoneToAdmin(phoneNumber);
    }

    @Override
    public void removePhoneFromAdmin(User user, int phoneId) {

        PermissionHelper.isAdmin(user, INVALID_PERMISSION);
        PhoneNumber phoneNumber = phoneRepository.getPhoneNumberById(phoneId);

        if (phoneNumber.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException(DO_NOT_HAVE_PERMISSION_TO_REMOVE_THIS_PHONE_NUMBER);
        }
        user.getPhoneNumbers().remove(phoneNumber);
        userRepository.update(user);
        phoneRepository.removePhoneFromAdmin(phoneNumber);
    }
}
