package com.kovacs.ferencz.HobbyHelper.repository;

import com.kovacs.ferencz.HobbyHelper.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Role entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
