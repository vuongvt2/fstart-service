package com.fstart.service.dao;

import com.fstart.service.entity.*;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.enumeration.EUserStatus;

import java.util.List;

/**
 * UserDAO
 *
 * @author: VuongVT2
 * @since: 2022/04/11
 */
public interface UserDAO {

    List<User> findByMajorAndPosition(List<Major> majors, List<Position> positions, EUserStatus status, long limit, long offset);

    long countByMajorAndPosition(List<Major> majors, List<Position> positions, EUserStatus status);

    List<User> findBySearchAndSkillAndFieldAndStatusAndPosition(final String search, final List<Technology> skills, final List<Field> fields, final String status, final ERole roleId, final List<Position> positions, final long limit, final long offset);

    long countBySearchAndSkillAndFieldAndStatusAndPosition(final String search, final List<Technology> skills, final List<Field> fields, final String status, final ERole roleId, final List<Position> positions);
}
