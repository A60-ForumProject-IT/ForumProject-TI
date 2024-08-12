package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.PermissionHelper;
import com.project.models.PhoneNumber;
import com.project.models.User;
import com.project.repositories.contracts.PhoneRepository;
import com.project.services.contracts.PhoneService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneServiceImpl implements PhoneService {
    private static final String INVALID_PERMISSION = "You dont have permissions! Only admins can do this operation.";
    private final PhoneRepository phoneRepository;

    @Autowired
    public PhoneServiceImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
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
}
