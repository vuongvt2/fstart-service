package com.fstart.service.model.discussion;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CommentForm
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentForm {
    public interface GroupCreate {
    }

    public interface GroupUpdate {
    }

    @NotNull(groups = GroupUpdate.class)
    private Long id;

    @NotBlank(groups = {GroupCreate.class, GroupUpdate.class})
    private String content;

    @NotNull(groups = GroupCreate.class)
    private Long discussionId;
}
