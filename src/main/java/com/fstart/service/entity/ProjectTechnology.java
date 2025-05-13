package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;

/**
 * ProjectTechnology
 *
 * @author: VuongVT2
 * @since: 2021/10/25
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_project_technology")
public class ProjectTechnology {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign Key
     */
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "tech_id", nullable = false)
    private Technology technology;

}