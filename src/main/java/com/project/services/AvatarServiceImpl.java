package com.project.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.models.Avatar;
import com.project.models.User;
import com.project.repositories.AvatarRepositoryImpl;
import com.project.repositories.contracts.AvatarRepository;
import com.project.repositories.contracts.UserRepository;
import com.project.services.contracts.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
public class AvatarServiceImpl implements AvatarService {
    private static final String UPLOAD_DIR = "Desktop/";
    public static final int DEFAULT_AVATAR = 1;
    private final Cloudinary cloudinary;
    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;

    @Autowired
    public AvatarServiceImpl(Cloudinary cloudinary, AvatarRepository avatarRepository, UserRepository userRepository) {
        this.cloudinary = cloudinary;
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }

//    @Override
//    public Avatar uploadAvatar(User user, MultipartFile avatarFile) throws IOException {
//        if (avatarFile.isEmpty()) {
//            throw new IllegalArgumentException("File is empty");
//        }
//
//        byte[] bytes = avatarFile.getBytes();
//        Path path = Paths.get("" + user.getId() + "_" + avatarFile.getOriginalFilename());
//        Files.write(path, bytes);
//
//        // Create and return the Avatar object
//        Avatar avatar = new Avatar();
//        avatar.setAvatar(path.toString());
//        avatarRepository.saveAvatar(avatar);
//
//        // Update user's avatar in the database
//        user.setAvatar(avatar);
//         userRepository.update(user);
//
//        return avatar;
//    }

    public Avatar uploadAvatar(User user, MultipartFile avatarFile) throws IOException {
        if (avatarFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        byte[] bytes = avatarFile.getBytes();
        Path path = Paths.get("src/main/resources/static/images/" + user.getId() + "_" + avatarFile.getOriginalFilename());
        Files.write(path, bytes);

        Avatar avatar = new Avatar();
        avatar.setAvatar(path.getFileName().toString());
        avatarRepository.saveAvatar(avatar);

        user.setAvatar(avatar);
        userRepository.update(user);

        return avatar;
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
        user.setAvatar(defaultAvatar);
        userRepository.update(user);
        return defaultAvatar;
    }

    @Override
    public Avatar initializeDefaultAvatar() {
        Avatar defaultAvatar = avatarRepository.getAvatarById(1);
        if (!"/images/DefaultUserAvatar.jpg".equals(defaultAvatar.getAvatar())) {
            throw new IllegalStateException("Avatar with ID 1 is not the default avatar");
        }
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
