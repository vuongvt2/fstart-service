package com.fstart.service.repository;

import com.fstart.service.entity.User;
import com.fstart.service.entity.UserField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserFieldRepository
 *
 * @author: VuongVT2
 * @since: 2021/11/13
 */
@Repository
public interface UserFieldRepository extends JpaRepository<UserField, Long> {

    List<UserField> findAllByUser(User user);

    void deleteAllByUser(User user);

}
