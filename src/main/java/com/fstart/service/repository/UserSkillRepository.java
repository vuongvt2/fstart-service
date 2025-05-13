package com.fstart.service.repository;

import com.fstart.service.entity.User;
import com.fstart.service.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserSkillRepository
 *
 * @author: VuongVT2
 * @since: 2021/11/13
 */
@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findAllByUser(User user);

    void deleteAllByUser(User user);

}
