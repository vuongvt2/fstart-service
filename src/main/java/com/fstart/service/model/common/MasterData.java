package com.fstart.service.model.common;

import lombok.*;

import java.util.List;

/**
 * MasterData
 *
 * @author: VuongVT2
 * @since: 2022/04/26
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MasterData {
    private List<CommonData> positions;
    private List<CommonData> technologies;
    private List<CommonData> fields;
    private List<CommonData> majors;
}
