package com.fstart.service.repository;

import com.fstart.service.entity.Country;
import com.fstart.service.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CountryRepository
 *
 * @author: VuongVT2
 * @since: 2021/11/06
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    Country findByStartups(Startup startup);
}