package com.project.repositories.contracts;

import com.project.models.Avatar;

public interface AvatarRepository {

    Avatar saveAvatar(Avatar avatar);

    Avatar getAvatarById(int id);

    void deleteAvatar(Avatar avatar);
}
