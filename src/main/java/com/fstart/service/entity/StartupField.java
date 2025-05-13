package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;

/**
 * StartupField
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
@Table(name = "fs_startup_field")
public class StartupField {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;
}
