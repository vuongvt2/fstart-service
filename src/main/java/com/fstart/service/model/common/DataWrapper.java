package com.fstart.service.model.common;

import lombok.*;

/**
 * DataWrapper
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataWrapper {

    private Object data;
    private String status;
    private String message;

}
