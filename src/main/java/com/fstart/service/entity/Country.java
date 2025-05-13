package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

/**
 * Country
 *
 * @author: VuongVT2
 * @since: 2021/11/05
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_country")
public class Country {

    @Id
    @Column(name = "id", length = 2, nullable = false)
    private String id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "country")
    private Collection<Startup> startups;
}
