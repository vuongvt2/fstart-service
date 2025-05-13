package com.fstart.service.model.project;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ProjectForm
 *
 * @author: VuongVT2
 * @since: 2022/01/16
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpsertForm {

    private String id;

    @NotBlank
    @Length(max = 150)
    private String title;

    @Length(max = 255)
    private String subTitle;

    @NotBlank
    private String description;

    @NotBlank
    @Length(max = 20)
    private String privacy;

    private String status;

    private boolean callForInvestment;

    @NotEmpty
    private List<String> fields;

    @NotEmpty
    private List<String> technologies;

    @NotNull
    private MultipartFile logo;

    private MultipartFile[] documents;

    private List<String> projectPositions; //BE,2,description

}
