package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;

/**
 * ProjectPosition
 *
 * @author: VuongVT2
 * @since: 2022/05/16
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_project_position")
public class ProjectPosition {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(name = "available_slot", nullable = false)
    private Long availableSlot;

    @Column(name = "description", length = 2000, nullable = false)
    private String description;
}
