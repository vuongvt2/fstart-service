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
 * Technology
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_technology")
public class Technology implements Serializable {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    // FK table fs_project_technology
    @JsonIgnore
    @OneToMany(mappedBy = "technology")
    private Collection<ProjectTechnology> projectTechnologies;

    // FK table fs_user_skill
    @JsonIgnore
    @OneToMany(mappedBy = "technology")
    private Collection<UserSkill> userSkills;

}
