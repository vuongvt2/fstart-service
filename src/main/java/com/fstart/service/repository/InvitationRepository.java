package com.fstart.service.repository;

import com.fstart.service.entity.Invitation;
import com.fstart.service.entity.Position;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.User;
import com.fstart.service.enumeration.EInvitationStatus;
import com.fstart.service.enumeration.EUserInvitationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long>, JpaSpecificationExecutor<Invitation> {

    @Query("select fi " +
            "from UserInvitation  fui " +
            "   left join Invitation fi on fui.invitation.id = fi.id " +
            "where " +
            "fui.user.id = :userId " +
            "and fui.type = :type " +
            "and fi.status = :status ")
    Page<Invitation> findAllByUserIdAndType(@Param("userId") String userId, @Param("type") EUserInvitationType type, EInvitationStatus status, Pageable pageable);

    @Query("select fi  " +
            "from Invitation fi  " +
            "         left join UserInvitation fui on fi.id = fui.invitation.id  " +
            "where fi.project.id = :projectId  " +
            "  and fui.user.id = :userId  " +
            "  and fi.status IN (:status)  " +
            "  and fui.type = :type " +
            "  and fi.position.id = :positionId " +
            "  and fi.type = 'REQUEST'")
    Invitation getInvitationBy(@Param("projectId") String projectId,
                               @Param("userId") String userId,
                               @Param("status") List<EInvitationStatus> statuses,
                               @Param("type") EUserInvitationType type,
                               @Param("positionId") String positionId);

    @Query("SELECT fi FROM Invitation fi " +
            "LEFT JOIN UserInvitation fui on fi.id = fui.invitation.id " +
            "WHERE fi.status = 'NEW' " +
            "AND fui.user.id = :user " +
            "AND fi.project.id = :project ")
    List<Invitation> getInvitationByUserAndProject(@Param("user") String userId,
                                                   @Param("project") String projectId);

    @Query("SELECT fi " +
            "FROM Invitation fi " +
            "LEFT JOIN UserInvitation fui ON fi.id = fui.invitation.id " +
            "WHERE fui.user.id = :userId " +
            "AND fi.project.id = :projectId " +
            "AND fui.type = :type " +
            "AND fi.status = :status")
    List<Invitation> findByUserAndProjectAndTypeAndStatus(@RequestParam(value = "userId") String userId,
                                                          @RequestParam(value = "projectId") String projectId,
                                                          @RequestParam(value = "type") String type,
                                                          @RequestParam(value = "status") String status);

    List<Invitation> findAllByProject(Project project);

    void deleteAllByProject(Project project);

    boolean existsByPositionAndProjectAndStatus(Position position, Project project, EInvitationStatus status);

    @Query("select count (fi.id) from Invitation fi left join UserInvitation fui on fi.id = fui.invitation.id " +
            " where fi.status = :status " +
            " and fi.position = :position " +
            " and fi.project = :project " +
            " and fui.user = :user ")
    Long countByPositionAndProjectAndUserAndStatus(Position position, Project project, User user, EInvitationStatus status);

    @Query("SELECT fi FROM Invitation fi " +
            "LEFT JOIN UserInvitation fui on fi.id = fui.invitation.id " +
            "WHERE " +
            " fui.user.id = :user " +
            "AND fi.project.id = :project ")
    List<Invitation> getListInvitationByUserAndProject(@Param("user") String userId,
                                                   @Param("project") String projectId);

}