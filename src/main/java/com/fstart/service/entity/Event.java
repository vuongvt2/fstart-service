package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * Event
 *
 * @author: VuongVT2
 * @since: 2022/03/02
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_event")
public class Event {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "banner", length = 1024, nullable = false)
    private String banner;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    @Column(name = "start_time", length = 14, nullable = false)
    private String startTime;

    @Column(name = "end_time", length = 14, nullable = false)
    private String endTime;

    // Keys
    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Collection<EventParticipant> eventParticipants;
}
