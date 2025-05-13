package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Vote
 *
 * @author: VuongVT2
 * @since: 2022/05/23
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_vote")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Keys
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Keys
    @ManyToOne
    @JoinColumn(name = "event_participant_id")
    private EventParticipant eventParticipant;

}
