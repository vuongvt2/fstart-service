package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ProjectUser
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
@Table(name = "fs_project_team_member")
public class ProjectTeamMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Keys
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
}
