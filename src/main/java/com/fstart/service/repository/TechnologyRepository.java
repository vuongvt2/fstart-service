package com.fstart.service.repository;

import com.fstart.service.entity.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, String> {
    List<Technology> findByIdIn(List<String> id);

}