package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.EInvitationStatus;
import com.fstart.service.enumeration.EInvitationType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Invitation
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_invitation")
public class Invitation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EInvitationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private EInvitationType type;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    // Keys
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @JsonIgnore
    @OneToMany(mappedBy = "invitation")
    private Collection<UserInvitation> userInvitations;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
}
