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
 * Major
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_major")
public class Major implements Serializable {

    @Id
    @Column(name = "id", length = 4, nullable = false)
    private String id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    // Keys
    @JsonIgnore
    @OneToMany(mappedBy = "major")
    private Collection<User> users;
}
