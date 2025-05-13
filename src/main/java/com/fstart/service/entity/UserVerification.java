package com.fstart.service.entity;

import com.fstart.service.enumeration.EUserVerificationStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * UserVerification
 *
 * @author VuongVT2
 * @since 2022/04/13
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_user_verification")
public class UserVerification implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hash", length = 36, nullable = false, unique = true)
    private String hash;

    @Column(name = "expire_time", length = 14, nullable = false)
    private String expireTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EUserVerificationStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
