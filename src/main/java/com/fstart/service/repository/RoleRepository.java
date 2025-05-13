package com.fstart.service.repository;

import com.fstart.service.entity.Role;
import com.fstart.service.enumeration.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, ERole> {

}