package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.EStartupStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * Startup
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
@Table(name = "fs_startup")
public class Startup {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "logo")
    private String logo;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "short_description", length = 150, nullable = false)
    private String shortDescription;

    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @Column(name = "original_link", length = 2048)
    private String originalLink;

    @Column(name = "founded")
    private int founded;

    @Column(name = "startup_size")
    private int startupSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EStartupStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "startup")
    private Collection<StartupFounder> startupFounders;

    @JsonIgnore
    @OneToMany(mappedBy = "startup")
    private Collection<StartupField> startupFields;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
