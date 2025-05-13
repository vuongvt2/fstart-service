package com.fstart.service.repository;

import com.fstart.service.entity.Invitation;
import com.fstart.service.entity.User;
import com.fstart.service.entity.UserInvitation;
import com.fstart.service.enumeration.EUserInvitationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserInvitationRepository
 *
 * @author: VuongVT2
 * @since: 2022/03/10
 */
@Repository
public interface UserInvitationRepository extends JpaRepository<UserInvitation, Long> {
    Optional<UserInvitation> findByInvitationAndType(Invitation invitation, EUserInvitationType type);

    boolean existsByInvitationAndUserAndType(Invitation invitation, User user, EUserInvitationType type);

    List<UserInvitation> findAllByInvitation(Invitation invitation);

    void deleteAllByInvitationIn(List<Invitation> invitations);


}
