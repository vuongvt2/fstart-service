package com.fstart.service.repository;

import com.fstart.service.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FieldRepository extends JpaRepository<Field, String> {
    List<Field> findByIdIn(List<String> fieldsId);
}