package com.project.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.models.Avatar;
import com.project.models.User;
import com.project.repositories.contracts.AvatarRepository;
import com.project.repositories.contracts.UserRepository;
import com.project.services.contracts.AvatarService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class AvatarServiceImpl implements AvatarService {
    public static final int DEFAULT_AVATAR = 1;
    private final Cloudinary cloudinary;
    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;

    public AvatarServiceImpl(Cloudinary cloudinary, AvatarRepository avatarRepository, UserRepository userRepository) {
        this.cloudinary = cloudinary;
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String uploadAvatar(User user, MultipartFile avatarFile) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(avatarFile.getBytes(), ObjectUtils.emptyMap());
        String avatarUrl = uploadResult.get("secure_url").toString();

        Avatar avatar = new Avatar();
        avatar.setAvatar(avatarUrl);
        avatarRepository.saveAvatar(avatar);

        user.setAvatar(avatar);
        userRepository.update(user);

        return avatarUrl;
    }

    @Override
    public Avatar getUserAvatar(User user) {
        if (user.getAvatar() != null) {
            return user.getAvatar();
        } else {
            return initializeDefaultAvatar(user);
        }
    }

    @Override
    public Avatar initializeDefaultAvatar(User user) {
        Avatar defaultAvatar = avatarRepository.getAvatarById(DEFAULT_AVATAR);
        userRepository.update(user);
        return defaultAvatar;
    }

    @Override
    public void deleteAvatarFromUser(User user) throws IOException {

        Avatar avatar = user.getAvatar();
        if (avatar != null) {
            String publicId = extractPublicIdFromUrl(avatar.getAvatar());
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            user.setAvatar(null);
            userRepository.update(user);
            avatarRepository.deleteAvatar(avatar);
        }
    }

    private String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        return publicIdWithExtension.split("\\.")[0];
    }
}
