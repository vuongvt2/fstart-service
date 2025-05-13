package com.fstart.service.repository;

import com.fstart.service.entity.Comment;
import com.fstart.service.entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CommentRepository
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Long countAllByDiscussion(Discussion discussion);

    @Query("SELECT fc " +
            "FROM Comment fc " +
            "WHERE fc.discussion = :discussion ")
    Page<Comment> findAllByDiscussion(@Param("discussion") Discussion discussion, Pageable pageable);

    void deleteAllByDiscussionIn(List<Discussion> discussions);

    void deleteAllByDiscussion(Discussion discussion);
}
