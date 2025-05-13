package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * userSkill
 *
 * @author: VuongVT2
 * @since: 2022/04/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_user_skill")
public class UserSkill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Foreign Key
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "technology_id")
    private Technology technology;
}