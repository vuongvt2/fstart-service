package com.fstart.service.model.project;

import lombok.*;

import java.util.List;

/**
 * AvailableProjectPositionData
 *
 * @author: VuongVT2
 * @since: 2022/05/22
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableProjectPositionData {
    private String projectId;
    private String projectTitle;
    private boolean inProject;
    private List<AvailableProjectPosition> projectPositions;
}
