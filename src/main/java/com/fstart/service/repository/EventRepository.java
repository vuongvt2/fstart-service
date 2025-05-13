package com.fstart.service.repository;

import com.fstart.service.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EventRepository
 *
 * @author: VuongVT2
 * @since: 2022/04/01
 */
@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    @Transactional(readOnly = true)
    @Query("SELECT fe " +
            "FROM Event fe " +
            "WHERE fe.startTime <= :currentTime " +
            "AND " +
            "unaccent(lower(fe.title)) like unaccent(lower(concat('%', :search ,'%' ))) " +
            "ORDER BY fe.startTime DESC")
    Page<Event> findAllEvents(Pageable pageable, String currentTime, String search);

    @Transactional(readOnly = true)
    @Query("SELECT fe " +
            "FROM Event fe " +
            "WHERE unaccent(lower(fe.title)) like unaccent(lower(concat('%', :search ,'%' ))) " +
            "ORDER BY fe.createdAt DESC")
    Page<Event> findAllEventsForAdmin(Pageable pageable, String search);

    @Query("SELECT fe FROM Event fe ORDER BY fe.startTime DESC ")
    List<Event> getTopEvent(Pageable pageable);
}
