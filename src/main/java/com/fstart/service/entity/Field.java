package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Field
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_field")
public class Field implements Serializable {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    // Keys
    @JsonIgnore
    @OneToMany(mappedBy = "field")
    private Collection<ProjectField> projectFields;

    @JsonIgnore
    @OneToMany(mappedBy = "field")
    private Collection<StartupField> startupFields;

    @JsonIgnore
    @OneToMany(mappedBy = "field")
    private Collection<UserField> userFields;


}
