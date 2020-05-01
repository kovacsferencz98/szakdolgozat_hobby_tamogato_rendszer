package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;

import javax.validation.constraints.*;
import java.util.Set;

public class AccountVM {
    @NotNull
    private Long userId;

    @NotBlank
    @Pattern(regexp = "^[_.@A-Za-z0-9-]*$")
    @Size(min = 1, max = 25)
    private String username;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(min = 2, max = 10)
    private String langKey;

    private Set<String> roles;

    @NotNull
    private Long detailsId;

    private Long profilePicId;

    @NotNull
    @Size(min = 1, max = 150)
    private String description;

    @NotNull
    private Long locationId;

    @NotNull
    private String country;

    private String region;

    @NotNull
    private String city;

    @NotNull
    private String street;

    @NotNull
    private Integer number;

    private String apartment;

    @NotNull
    private String zip;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;


    public static AccountVM createAccountVM(UserDTO userDTO, LocationDTO locationDTO, UserDetailsDTO userDetailsDTO) {
        AccountVM account = new AccountVM();
        account.setUserId(userDTO.getId());
        account.setFirstName(userDTO.getFirstName());
        account.setLastName(userDTO.getLastName());
        account.setEmail(userDTO.getEmail());
        account.setLangKey(userDTO.getLangKey());
        account.setUsername(userDTO.getUsername());
        account.setRoles(userDTO.getRoles());
        account.setUserDetailsId(userDetailsDTO.getId());
        account.setDescription(userDetailsDTO.getDescription());
        account.setDetailsId(userDetailsDTO.getId());
        account.setProfilePicId(userDetailsDTO.getProfilePicId());
        account.setLocationId(locationDTO.getId());
        account.setCountry(locationDTO.getCountry());
        account.setRegion(locationDTO.getRegion());
        account.setCity(locationDTO.getCity());
        account.setStreet(locationDTO.getStreet());
        account.setNumber(locationDTO.getNumber());
        account.setApartment(locationDTO.getApartment());
        account.setZip(locationDTO.getZip());
        account.setLatitude(locationDTO.getLatitude());
        account.setLongitude(locationDTO.getLongitude());
        return account;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserDetailsId() {
        return detailsId;
    }

    public void setUserDetailsId(Long userDetailsId) {
        this.detailsId = userDetailsId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(Long detailsId) {
        this.detailsId = detailsId;
    }

    public Long getProfilePicId() {
        return profilePicId;
    }

    public void setProfilePicId(Long profilePicId) {
        this.profilePicId = profilePicId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountVM accountVM = (AccountVM) o;

        if (userId != null ? !userId.equals(accountVM.userId) : accountVM.userId != null) return false;
        if (username != null ? !username.equals(accountVM.username) : accountVM.username != null) return false;
        if (firstName != null ? !firstName.equals(accountVM.firstName) : accountVM.firstName != null) return false;
        if (lastName != null ? !lastName.equals(accountVM.lastName) : accountVM.lastName != null) return false;
        if (email != null ? !email.equals(accountVM.email) : accountVM.email != null) return false;
        if (langKey != null ? !langKey.equals(accountVM.langKey) : accountVM.langKey != null) return false;
        if (roles != null ? !roles.equals(accountVM.roles) : accountVM.roles != null) return false;
        if (detailsId != null ? !detailsId.equals(accountVM.detailsId) : accountVM.detailsId != null) return false;
        if (profilePicId != null ? !profilePicId.equals(accountVM.profilePicId) : accountVM.profilePicId != null)
            return false;
        if (description != null ? !description.equals(accountVM.description) : accountVM.description != null)
            return false;
        if (locationId != null ? !locationId.equals(accountVM.locationId) : accountVM.locationId != null) return false;
        if (country != null ? !country.equals(accountVM.country) : accountVM.country != null) return false;
        if (region != null ? !region.equals(accountVM.region) : accountVM.region != null) return false;
        if (city != null ? !city.equals(accountVM.city) : accountVM.city != null) return false;
        if (street != null ? !street.equals(accountVM.street) : accountVM.street != null) return false;
        if (number != null ? !number.equals(accountVM.number) : accountVM.number != null) return false;
        if (apartment != null ? !apartment.equals(accountVM.apartment) : accountVM.apartment != null) return false;
        if (zip != null ? !zip.equals(accountVM.zip) : accountVM.zip != null) return false;
        if (latitude != null ? !latitude.equals(accountVM.latitude) : accountVM.latitude != null) return false;
        return longitude != null ? longitude.equals(accountVM.longitude) : accountVM.longitude == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (langKey != null ? langKey.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (detailsId != null ? detailsId.hashCode() : 0);
        result = 31 * result + (profilePicId != null ? profilePicId.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (locationId != null ? locationId.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (apartment != null ? apartment.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }
}
