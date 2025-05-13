package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Role
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_role")
public class Role implements Serializable {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "id", length = 20, nullable = false)
    private ERole id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    // Keys
    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Collection<User> users;
}
