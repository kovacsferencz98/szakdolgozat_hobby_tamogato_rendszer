package com.kovacs.ferencz.HobbyHelper.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.kovacs.ferencz.HobbyHelper.domain.UserDetails} entity.
 */
public class UserDetailsDTO implements Serializable {

    private Long id;

    @NotNull
    private String description;

    private Long profilePicId;

    private Long userId;

    private String userUsername;

    private Long residenceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProfilePicId() {
        return profilePicId;
    }

    public void setProfilePicId(Long profilePicId) {
        this.profilePicId = profilePicId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userAId) {
        this.userId = userAId;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userAUsername) {
        this.userUsername = userAUsername;
    }

    public Long getResidenceId() {
        return residenceId;
    }

    public void setResidenceId(Long locationId) {
        this.residenceId = locationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDetailsDTO that = (UserDetailsDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (profilePicId != null ? !profilePicId.equals(that.profilePicId) : that.profilePicId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userUsername != null ? !userUsername.equals(that.userUsername) : that.userUsername != null) return false;
        return residenceId != null ? residenceId.equals(that.residenceId) : that.residenceId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (profilePicId != null ? profilePicId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userUsername != null ? userUsername.hashCode() : 0);
        result = 31 * result + (residenceId != null ? residenceId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDetailsDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", profilePic='" + getProfilePicId() + "'" +
            ", user=" + getUserId() +
            ", user='" + getUserUsername() + "'" +
            ", residence=" + getResidenceId() +
            "}";
    }
}
