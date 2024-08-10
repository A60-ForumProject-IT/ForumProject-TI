package com.project.services.contracts;

import com.project.models.Avatar;
import com.project.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {
    Avatar getUserAvatar(User user);

    String uploadAvatar(User user, MultipartFile avatarFile) throws IOException;

    void deleteAvatarFromUser(User user) throws IOException;

    Avatar initializeDefaultAvatar(User user);
}
