package com.fstart.service.model.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * ProjectDocumentForm
 *
 * @author: VuongVT2
 * @since: 2022/05/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDocumentForm {

    @NotBlank
    @Length(max = 10)
    private String projectId;
    private MultipartFile[] documents;
}
