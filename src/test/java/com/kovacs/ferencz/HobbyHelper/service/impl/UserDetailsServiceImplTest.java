package com.kovacs.ferencz.HobbyHelper.service.impl;

import com.kovacs.ferencz.HobbyHelper.domain.*;
import com.kovacs.ferencz.HobbyHelper.repository.UserDetailsRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.PictureService;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserDetailsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserDetailsServiceImplTest {

    @MockBean
    private UserDetailsRepository userDetailsRepository;

    @MockBean
    private UserDetailsMapper userDetailsMapper;

    @MockBean
    private LocationService locationService;

    @MockBean
    private PictureService pictureService;

    @Autowired
    private UserDetailsServiceImpl underTest;

    private UserDetails userDetails;

    private UserDetailsDTO userDetailsDTO;

    @BeforeEach
    void setUp() {
        userDetails = initUserDetails();
        userDetailsDTO = initUserDetailsDTO();
    }

    @Test
    void saveShouldSaveEntity() {
        //GIVEN
        given(userDetailsMapper.toEntity(any(UserDetailsDTO.class))).willReturn(userDetails);
        given(userDetailsMapper.toDto(any(UserDetails.class))).willReturn(userDetailsDTO);
        given(userDetailsRepository.save(any(UserDetails.class))).willReturn(userDetails);
        //WHEN
        UserDetailsDTO result = underTest.save(userDetailsDTO);
        //THEN
        BDDMockito.verify(userDetailsRepository).save(userDetails);
        assertEquals(userDetailsDTO, result);
    }

    @Test
    void findAllShouldReturnAllEntities() {
        //GIVEN
        given(userDetailsRepository.findAll()).willReturn(Arrays.asList(userDetails));
        given(userDetailsMapper.toDto(any(UserDetails.class))).willReturn(userDetailsDTO);
        //WHEN
        List<UserDetailsDTO> result = underTest.findAll();
        //THEN
        verify(userDetailsRepository).findAll();
        assertEquals(Arrays.asList(userDetailsDTO), result);
    }

    @Test
    void findOneShouldReturnFoundEntity() {
        //GIVEN
        given(userDetailsRepository.findById(anyLong())).willReturn(Optional.of(userDetails));
        given(userDetailsMapper.toDto(any(UserDetails.class))).willReturn(userDetailsDTO);
        //WHEN
        Optional<UserDetailsDTO> result = underTest.findOne(1L);
        //THEN
        verify(userDetailsRepository).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(userDetailsDTO, result.get());
    }


    @Test
    void findAllByResidenceShouldReturnEntities() {
        //GIVEN
        given(userDetailsRepository.findAllByResidence_Id(anyLong())).willReturn(Arrays.asList(userDetails));
        given(userDetailsMapper.toDto(any(UserDetails.class))).willReturn(userDetailsDTO);
        //WHEN
        List<UserDetailsDTO> result = underTest.findAllByResidence(1L);
        //THEN
        verify(userDetailsRepository).findAllByResidence_Id(1L);
        assertEquals(Arrays.asList(userDetailsDTO), result);
    }

    @Test
    void findByUserIdShouldReturnFoundEntity() {
        //GIVEN
        given(userDetailsRepository.findOneByUser_Id(anyLong())).willReturn(Optional.of(userDetails));
        given(userDetailsMapper.toDto(any(UserDetails.class))).willReturn(userDetailsDTO);
        //WHEN
        Optional<UserDetailsDTO> result = underTest.findByUserId(1L);
        //THEN
        verify(userDetailsRepository).findOneByUser_Id(1L);
        assertTrue(result.isPresent());
        assertEquals(userDetailsDTO, result.get());
    }

    @Test
    void deleteShouldDeleteEntity() {
        //GIVEN
        given(userDetailsRepository.findOneByUser_Id(anyLong())).willReturn(Optional.of(userDetails));
        given(userDetailsRepository.findById(anyLong())).willReturn(Optional.of(userDetails));
        given(userDetailsMapper.toDto(any(UserDetails.class))).willReturn(userDetailsDTO);
        //WHEN
        underTest.deleteDetailOfUser(userDetails.getUser().getId());
        //THEN
        InOrder inOrder = Mockito.inOrder(userDetailsRepository, locationService, pictureService);
        inOrder.verify(userDetailsRepository).findOneByUser_Id(userDetailsDTO.getUserId());
        inOrder.verify(userDetailsRepository).findById(userDetailsDTO.getId());
        inOrder.verify(userDetailsRepository).deleteById(userDetailsDTO.getId());
        inOrder.verify(locationService).delete(userDetailsDTO.getResidenceId());
        inOrder.verify(pictureService).deleteFile(userDetailsDTO.getProfilePicId());
    }

    public UserDetailsDTO initUserDetailsDTO() {
        UserDetailsDTO result = new UserDetailsDTO();
        result.setProfilePicId(1L);
        result.setUserUsername("user");
        result.setUserId(1L);
        result.setResidenceId(1L);
        result.setDescription("Description");
        result.setId(1L);
        return result;
    }

    private Picture initPicture() {
        Picture result = new Picture();
        result.setData(new byte[]{1});
        result.setFileName("picture");
        result.setFileType("image/jpeg");
        result.setId(1L);
        return result;
    }

    public UserDetails initUserDetails() {
        UserDetails result = new UserDetails();
        result.setProfilePic(initPicture());
        result.setUser(initUser());
        result.setResidence(initLocation());
        result.setDescription("Description");
        result.setId(1L);
        return result;
    }

    private Location initLocation() {
        Location result = new Location();
        result.setId(1L);
        result.setApartment("1A");
        result.setCity("Debrecen");
        result.setCountry("Hungary");
        result.setRegion("Hajdu-Bihar");
        result.setLatitude(42.0);
        result.setLongitude(27.0);
        result.setZip("4031");
        return result;
    }

    private User initUser() {
        User result = new User();
        result.setLangKey("en");
        result.setLastName("Last");
        result.setFirstName("First");
        result.setEmail("email@email.com");
        result.setRoles(new HashSet(Arrays.asList(new Role(AuthoritiesConstants.USER))));
        result.setId(1L);
        result.setUsername("user");
        result.setActivated(false);
        result.setPassword("pwdpwdpwd");
        return result;
    }
}