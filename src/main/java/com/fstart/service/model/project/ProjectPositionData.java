package com.fstart.service.model.project;

import com.fstart.service.model.common.CommonData;
import lombok.*;

/**
 * ProjectPositionData
 *
 * @author: VuongVT2
 * @since: 2022/05/16
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPositionData {
    private Long id;
    private CommonData position;
    private Long availableSlot;
    private String description;
    private boolean requested;
}
