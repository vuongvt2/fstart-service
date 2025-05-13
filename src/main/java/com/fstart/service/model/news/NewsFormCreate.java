package com.fstart.service.model.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * NewsForm
 *
 * @author: VuongVT2
 * @since: 2022/03/04
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsFormCreate implements Serializable {

    @NotBlank
    @Length(max = 150)
    private String title;

    @NotNull
    private MultipartFile thumbnail;

    @NotBlank
    @Length(max = 2000)
    private String shortDescription;

    @NotBlank
    private String description;

    private String tags;

}
