package com.fstart.service.model.project;

import lombok.*;

import java.io.Serializable;

/**
 * DocumentData
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentData implements Serializable {

    private Long id;
    private String name;
    private String link;
}
