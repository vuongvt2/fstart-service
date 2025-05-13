package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.EEventParticipantStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * EventParticipant
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
@Table(name = "fs_event_participant")
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EEventParticipantStatus status;

    // Keys
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @JsonIgnore
    @OneToMany(mappedBy = "eventParticipant")
    private Collection<Discussion> discussions;

    @JsonIgnore
    @OneToMany(mappedBy = "eventParticipant")
    private Collection<Vote> votes;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "eventParticipant")
    private Collection<Document> documents;
}
