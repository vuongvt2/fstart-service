package com.fstart.service.model.discussion;

import lombok.*;

/**
 * CommentData
 *
 * @author: VuongVT2
 * @since: 2022/02/16
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentData {
    private Long id;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String userComment;
    private String avatarUserComment;
    private boolean commenter;
    private String userId;
}
