package com.fstart.service.model.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * NewsFormUpdate
 *
 * @author: VuongVT2
 * @since: 2022/05/15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsFormUpdate {

    @NotBlank
    @Length(max = 10)
    private String id;

    @NotBlank
    @Length(max = 150)
    private String title;

    private MultipartFile thumbnail;

    @NotBlank
    @Length(max = 2000)
    private String shortDescription;

    @NotBlank
    private String description;

    private String tags;

}
