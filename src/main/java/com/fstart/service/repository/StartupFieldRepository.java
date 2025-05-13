package com.fstart.service.repository;

import com.fstart.service.entity.Startup;
import com.fstart.service.entity.StartupField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StartupFieldRepository
 *
 * @author: VuongVT2
 * @since: 2022/01/18
 */
@Repository
public interface StartupFieldRepository extends JpaRepository<StartupField, Long> {

    List<StartupField> findByStartup(Startup startup);

    void deleteAllByStartup(Startup startup);
}
