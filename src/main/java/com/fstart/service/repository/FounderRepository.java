package com.fstart.service.repository;

import com.fstart.service.entity.Founder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * FounderRepository
 *
 * @author: VuongVT2
 * @since: 2022/04/13
 */
@Repository
public interface FounderRepository extends JpaRepository<Founder, Long> {
    Founder findByNameAndSocialNetwork(String name, String socialNetwork);
}
