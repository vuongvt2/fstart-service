package com.fstart.service.model.discussion;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ProjectDiscussionUpdate
 *
 * @author: VuongVT2
 * @since: 2022/05/27
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    @Length(max = 2000)
    private String content;
}
