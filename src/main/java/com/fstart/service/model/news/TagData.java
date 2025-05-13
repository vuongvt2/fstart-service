package com.fstart.service.model.news;

import lombok.*;

import java.io.Serializable;

/**
 * TagData
 *
 * @author: VuongVT2
 * @since: 2022/04/12
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagData implements Serializable {
    private Long id;
    private String name;
}
