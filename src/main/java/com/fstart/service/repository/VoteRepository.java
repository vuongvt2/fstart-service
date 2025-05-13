package com.fstart.service.repository;

import com.fstart.service.entity.EventParticipant;
import com.fstart.service.entity.User;
import com.fstart.service.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * VoteRepository
 *
 * @author: VuongVT2
 * @since: 2022/05/23
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Long countAllByEventParticipant(EventParticipant eventParticipant);

    boolean existsByEventParticipantAndUser(EventParticipant eventParticipant, User user);

    void deleteAllByEventParticipant(EventParticipant eventParticipant);

    Vote findByEventParticipantAndUser(EventParticipant eventParticipant, User user);
}
