package com.fstart.service.repository;

import com.fstart.service.entity.Discussion;
import com.fstart.service.entity.EventParticipant;
import com.fstart.service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DiscussionRepository
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    List<Discussion> findAllByProject(Project project);

    List<Discussion> findAllDiscussionsByEventParticipant(EventParticipant eventParticipant);

    @Query("SELECT fd FROM Discussion fd WHERE unaccent(lower(fd.content)) like unaccent(lower(concat('%', :content ,'%' )))")
    List<Discussion> findAllByContent(@Param("content") String content);

    List<Discussion> findAllByEventParticipant(EventParticipant eventParticipant);

}
