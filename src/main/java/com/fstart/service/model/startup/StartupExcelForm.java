package com.fstart.service.model.startup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * StartupExcelForm
 *
 * @author: VuongVT2
 * @since: 2022/04/16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartupExcelForm {

    private Long id;

    private String logo;

    @NotBlank
    @Length(max = 150)
    private String name;

    @NotBlank
    @Length(max = 150)
    private String shortDescription;

    @NotBlank
    @Length(max = 2000)
    private String description;

    @Length(max = 2048)
    private String originalLink;

    private int founded;

    private int startupSize;

    @NotBlank
    @Length(max = 20)
    private String status;

    private String countryId;

}
