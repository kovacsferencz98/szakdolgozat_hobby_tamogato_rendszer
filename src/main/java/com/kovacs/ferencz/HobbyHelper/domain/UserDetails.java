package com.kovacs.ferencz.HobbyHelper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A UserDetails.
 */
@Entity
@Table(name = "user_details")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="picture_id", referencedColumnName = "id", unique = true, nullable = true)
    private Picture profilePic;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "residence", referencedColumnName = "id")
    @JsonIgnoreProperties("userDetails")
    private Location residence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public UserDetails description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Picture getProfilePic() {
        return profilePic;
    }

    public UserDetails profilePic(Picture profilePic) {
        this.profilePic = profilePic;
        return this;
    }

    public void setProfilePic(Picture profilePic) {
        this.profilePic = profilePic;
    }

    public User getUser() {
        return user;
    }

    public UserDetails user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getResidence() {
        return residence;
    }

    public UserDetails residence(Location location) {
        this.residence = location;
        return this;
    }

    public void setResidence(Location location) {
        this.residence = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDetails that = (UserDetails) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (profilePic != null ? !profilePic.getId().equals(that.profilePic.getId()) : that.profilePic != null) return false;
        if (user != null ? !user.getId().equals(that.user.getId()) : that.user != null) return false;
        return residence != null ? residence.getId().equals(that.residence.getId()) : that.residence == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (profilePic != null ? profilePic.getId().hashCode() : 0);
        result = 31 * result + (user != null ? user.getId().hashCode() : 0);
        result = 31 * result + (residence != null ? residence.getId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", profilePic=" + (profilePic!=null ? profilePic.getId() : "-") +
                ", user=" + (user!=null ? user.getId() : "-") +
                ", residence=" + (residence!=null ? residence.getId() : "-") +
                '}';
    }
}
