package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * Founder
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
@Table(name = "fs_founder")
public class Founder {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "social_network", length = 2048, nullable = false, unique = true)
    private String socialNetwork;

    // Keys
    @JsonIgnore
    @OneToMany(mappedBy = "founder")
    private Collection<StartupFounder> startupFounders;
}
