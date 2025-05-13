package com.fstart.service.entity;

import com.fstart.service.enumeration.EUserInvitationType;
import lombok.*;

import javax.persistence.*;

/**
 * UserInvitation
 *
 * @author: VuongVT2
 * @since: 2022/03/04
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_user_invitation")
public class UserInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private EUserInvitationType type;

    // Keys
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "invitation_id", nullable = false)
    private Invitation invitation;
}
