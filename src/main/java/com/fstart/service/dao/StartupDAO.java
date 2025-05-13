package com.fstart.service.dao;

import com.fstart.service.entity.Field;
import com.fstart.service.entity.Startup;

import java.util.List;

/**
 * StartupDAO
 *
 * @author: VuongVT2
 * @since: 2022/05/20
 */
public interface StartupDAO {
    List<Startup> findBySearchAndField(String search, List<Field> fields, long page, long size);

    Long countBySearchAndField(String search, List<Field> fields, long page, long size);
}