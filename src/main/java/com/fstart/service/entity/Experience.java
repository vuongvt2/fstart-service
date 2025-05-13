package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Experience
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
@Table(name = "fs_experience")
public class Experience implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "address", length = 500, nullable = false)
    private String address;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "start_working", length = 8, nullable = false)
    private String startWorking;

    @Column(name = "end_working", length = 8)
    private String endWorking;

    @Column(name = "current_working_flg", nullable = false)
    private boolean currentWorkingFlg;

    // Keys
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}