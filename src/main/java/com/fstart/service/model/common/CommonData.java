package com.fstart.service.model.common;

import lombok.*;

import java.io.Serializable;

/**
 * FieldData
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonData implements Serializable {

    private String id;
    private String name;

}
