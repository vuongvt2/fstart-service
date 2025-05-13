package com.fstart.service.repository;

import com.fstart.service.entity.Experience;
import com.fstart.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findAllByUser(User user);

    void deleteAllByUser(User user);

    boolean existsByUser(User user);

}