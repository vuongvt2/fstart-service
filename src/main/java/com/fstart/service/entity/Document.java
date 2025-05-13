package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Document
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_document")
public class Document implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "link", length = 2048, nullable = false)
    private String link;

    // Keys
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "event_participant_id")
    private EventParticipant eventParticipant;
}
