package com.fstart.service.model.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * NewsFileForm
 *
 * @author: VuongVT2
 * @since: 2022/05/27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsFileForm {

    private String newsId;
    private MultipartFile[] files;
}
