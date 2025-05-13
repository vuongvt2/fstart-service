package com.fstart.service.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Comment
 *
 * @author: VuongVT2
 * @since: 2021/10/29
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", length = 2000, nullable = false)
    private String content;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    // Keys
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;
}
