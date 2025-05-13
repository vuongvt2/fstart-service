package com.fstart.service.repository;

import com.fstart.service.entity.Startup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StartupRepository
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {

    @Query("SELECT fs FROM Startup fs ORDER BY RANDOM()")
    List<Startup> getTopStartup(Pageable pageable);
}
