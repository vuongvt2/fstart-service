package com.fstart.service.model.discussion;

import com.fstart.service.model.common.PagedResponse;
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
public class DiscussionDetailData {
    private DiscussionData discussionData;
    private PagedResponse<CommentData> commentDataList;
}
