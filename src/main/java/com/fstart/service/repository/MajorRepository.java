package com.fstart.service.repository;

import com.fstart.service.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, String> {

    @Query("SELECT fm FROM Major fm LEFT JOIN User fu ON fm.id = fu.major.id where fu.id = :userId")
    Major findByUser(@Param("userId") String userId);
}