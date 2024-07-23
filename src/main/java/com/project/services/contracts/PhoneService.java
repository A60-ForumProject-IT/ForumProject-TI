package com.project.services.contracts;

import com.project.models.PhoneNumber;
import com.project.models.User;

public interface PhoneService {
    void addPhoneToAnAdmin(User userToAddPhone, PhoneNumber phoneNumber);
}
