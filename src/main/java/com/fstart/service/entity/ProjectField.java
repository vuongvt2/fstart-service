package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ProjectField
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
@Table(name = "fs_project_field")
public class ProjectField implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Foreign Key
     */
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;
}