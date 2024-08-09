package com.project.services.contracts;

import com.project.models.Avatar;
import com.project.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {
    public Avatar getAvatarByUser(User user);

    public String uploadAvatar(User user, MultipartFile avatarFile) throws IOException;

    public void deleteAvatarFromUser(User user) throws IOException;
}
