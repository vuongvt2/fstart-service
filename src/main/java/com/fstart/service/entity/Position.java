package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * Position
 *
 * @author: VuongVT2
 * @since: 2022/03/21
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_position")
public class Position {

    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "position")
    private Collection<UserPosition> userPositions;

    @JsonIgnore
    @OneToMany(mappedBy = "position")
    private Collection<ProjectTeamMember> projectTeamMembers;

    @JsonIgnore
    @OneToMany(mappedBy = "position")
    private Collection<Invitation> invitations;

    @JsonIgnore
    @OneToMany(mappedBy = "position")
    private Collection<ProjectPosition> projectPositions;
}
