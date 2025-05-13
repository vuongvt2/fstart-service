package com.fstart.service.model.discussion;

import lombok.*;

/**
 * DiscussionData
 *
 * @author: VuongVT2
 * @since: 2022/02/16
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionData {
    private Long id;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String fullName;
    private Long numberOfComment;
    private String avatar;
    private boolean owner;
    private String userId;
}
