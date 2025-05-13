package com.fstart.service.model.project;

import com.fstart.service.model.common.CommonData;
import lombok.*;

/**
 * AvailableProjectPosition
 *
 * @author: VuongVT2
 * @since: 2022/05/22
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableProjectPosition {
    private Long Id;
    private Long availableSlot;
    private CommonData position;
    private boolean invited;
}
