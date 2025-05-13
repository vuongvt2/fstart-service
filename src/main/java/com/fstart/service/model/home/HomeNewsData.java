package com.fstart.service.model.home;

import lombok.*;

import java.util.List;

/**
 * HomeNewsData
 *
 * @author: VuongVT2
 * @since: 2022/04/13
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeNewsData {
    private String id;
    private String thumbnail;
    private String title;
    private String shortDescription;
    private List<String> tags;

}
