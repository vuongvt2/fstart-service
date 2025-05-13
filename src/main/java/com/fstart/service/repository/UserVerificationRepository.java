package com.fstart.service.repository;

import com.fstart.service.entity.User;
import com.fstart.service.entity.UserVerification;
import com.fstart.service.enumeration.EUserVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserVerificationRepository
 *
 * @author VuongVT2
 * @since 2022/04/13
 */
@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {

    Optional<UserVerification> findByUserAndHashAndStatus(User user, String hash, EUserVerificationStatus status);
    List<UserVerification> findAllByUserAndStatus(User user, EUserVerificationStatus status);
    boolean existsByHash(String hash);

}
