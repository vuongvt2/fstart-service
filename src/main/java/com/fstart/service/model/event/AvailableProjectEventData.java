package com.fstart.service.model.event;

import lombok.*;

/**
 * AvailableProjectEventData
 *
 * @author: VuongVT2
 * @since: 2022/06/04
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableProjectEventData {
    private String projectId;
    private String projectTitle;
    private boolean inEvent;
}
