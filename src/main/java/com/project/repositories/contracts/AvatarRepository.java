package com.project.repositories.contracts;

import com.project.models.Avatar;

public interface AvatarRepository {

    public Avatar saveAvatar(Avatar avatar);

    public Avatar getAvatarById(int id);

    public void deleteAvatar(Avatar avatar);
}
