package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.EDiscussionStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Discussion
 *
 * @author: VuongVT2
 * @since: 2021/10/29
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_discussion")
public class Discussion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", length = 2000, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EDiscussionStatus status;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    // Keys
    @JsonIgnore
    @OneToMany(mappedBy = "discussion")
    private Collection<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "event_participant_id")
    private EventParticipant eventParticipant;
}
