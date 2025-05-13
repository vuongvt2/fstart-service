package com.fstart.service.model.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProjectPositionForm
 *
 * @author: VuongVT2
 * @since: 2022/05/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPositionForm {
    private Long id;
    private Long availableSlot;
    private String description;
    private String positionId;
    private boolean delete;
}
