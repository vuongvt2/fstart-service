package com.fstart.service.repository;

import com.fstart.service.entity.Position;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.ProjectTeamMember;
import com.fstart.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * GroupRoleRepository
 *
 * @author: VuongVT2
 * @since: 2021/11/13
 */
@Repository
public interface ProjectTeamMemberRepository extends JpaRepository<ProjectTeamMember, Long> {

    Optional<ProjectTeamMember> findByUser(User user);

    List<ProjectTeamMember> findByProject(Project project);

    List<ProjectTeamMember> findAllByUser(User user);

    int countAllByProject(Project project);

    ProjectTeamMember findByProjectAndPosition(Project project, Position position);

    void deleteAllByProject(Project project);

    List<ProjectTeamMember> findAllByProjectAndPositionAndUserIsNull(Project project, Position position);

    List<ProjectTeamMember> findAllByProjectAndUserIsNull(Project project);

    List<ProjectTeamMember> findByProjectAndUserIsNotNull(Project project);

    List<ProjectTeamMember> findByProjectAndUserIsNull(Project project);

    boolean existsByProjectAndUserAndPosition(Project project, User user, Position position);

    boolean existsByProjectAndUser(Project project, User user);

    @Query("select count(fui.user) from UserInvitation fui " +
            "left join Invitation fi on fi.id = fui.invitation.id " +
            "where fui.user.id = :userId " +
            "and fi.project.id = :projectId " +
            "and fi.position.id = :positionId " +
            "and fi.status != 'CANCEL'")
    Long countExistByUserAndProjectAndPosition(@Param("userId") String userId, @Param("projectId") String projectId, @Param("positionId") String positionId);

    boolean existsByUserAndPosition(User user, Position position);

    int countAllByProjectAndUserIsNotNull(Project project);

    ProjectTeamMember findByProjectAndUser(Project project, User user);

    void deleteAllByProjectAndPositionNot(Project project, Position position);

    boolean existsByProjectAndUserAndPositionNot(Project project, User user, Position position);

    void deleteByUserAndProject(User user, Project project);

    List<ProjectTeamMember> findAllByUserAndPosition(User user, Position position);

}
