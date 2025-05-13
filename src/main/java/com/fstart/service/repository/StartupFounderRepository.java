package com.fstart.service.repository;

import com.fstart.service.entity.Startup;
import com.fstart.service.entity.StartupFounder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StartupFounderRepository
 *
 * @author: VuongVT2
 * @since: 2022/01/18
 */
@Repository
public interface StartupFounderRepository extends JpaRepository<StartupFounder, Long> {

    List<StartupFounder> findByStartup(Startup startup);

    void deleteAllByStartup(Startup startup);
}
