package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * News
 *
 * @author: VuongVT2
 * @since: 2022/03/02
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_news")
public class News {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "thumbnail", length = 1024, nullable = false)
    private String thumbnail;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "short_description", length = 2000, nullable = false)
    private String shortDescription;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    // Keys
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "tags")
    private String tags;

}
