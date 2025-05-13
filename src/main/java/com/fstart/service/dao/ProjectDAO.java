package com.fstart.service.dao;

import com.fstart.service.entity.Field;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.Technology;

import java.util.List;

/**
 * ProjectDAO
 *
 * @author VuongVT2
 * @since 2022/04/11
 */
public interface ProjectDAO {

    List<Project> findBySearchAndTechnologyAndFieldAndStatus(String search, List<Technology> technologies, List<Field> fields, String status, long limit, long offset);

    long countBySearchAndTechnologyAndField(String search, List<Technology> technologies, List<Field> fields, String status);

}
