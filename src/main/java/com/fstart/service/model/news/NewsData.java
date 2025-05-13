package com.fstart.service.model.news;

import com.fstart.service.model.user.BaseUserData;
import lombok.*;

import java.util.List;

/**
 * NewsData
 *
 * @author: VuongVT2
 * @since: 2022/04/12
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsData {
    private String id;
    private String thumbnail;
    private String title;
    private String shortDescription;
    private String description;
    private String createdAt;
    private String updatedAt;
    private BaseUserData user;
    private List<String> tags;
}
