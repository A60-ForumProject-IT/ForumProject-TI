package com.project.repositories.contracts;

import com.project.models.PhoneNumber;

public interface PhoneRepository {
    void addPhoneToAdmin(PhoneNumber phoneNumber);

    PhoneNumber getPhoneNumber(String phoneNumber);

    void removePhoneFromAdmin(PhoneNumber phoneNumber);

    PhoneNumber getPhoneNumberById(int phoneId);
}
