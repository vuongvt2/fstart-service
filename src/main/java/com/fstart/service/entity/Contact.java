package com.fstart.service.entity;

import com.fstart.service.enumeration.EContactStatus;
import lombok.*;

import javax.persistence.*;

/**
 * Contact
 *
 * @author: VuongVT2
 * @since: 2022/05/25
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "fullName", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", length = 320, nullable = false)
    private String email;

    @Column(name = "message", length = 2000, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EContactStatus status;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;
}
