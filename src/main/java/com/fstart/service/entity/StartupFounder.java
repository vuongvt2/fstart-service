package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;

/**
 * StartupFounder
 *
 * @author: VuongVT2
 * @since: 2021/11/05
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_startup_founder")
public class StartupFounder {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @ManyToOne
    @JoinColumn(name = "founder_id", nullable = false)
    private Founder founder;
}
