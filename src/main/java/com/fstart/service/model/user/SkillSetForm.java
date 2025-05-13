package com.fstart.service.model.user;

import lombok.*;

import java.util.List;

/**
 * SkillSetForm
 *
 * @author: VuongVT2
 * @since: 2022/04/15
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillSetForm {
    private List<String> positionsId;
    private List<String> fieldsId;
    private List<String> technologiesId;
}
