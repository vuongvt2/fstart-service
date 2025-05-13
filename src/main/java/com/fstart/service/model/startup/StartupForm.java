package com.fstart.service.model.startup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * StartupForm
 *
 * @author: VuongVT2
 * @since: 2022/01/17
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartupForm {

    public interface StartupUpdate {
    }

    @NotBlank(groups = StartupUpdate.class)
    @Length(max = 10, groups = StartupUpdate.class)
    private Long id;

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

    @NotEmpty
    private List<FounderData> startupFounders;

    @NotEmpty
    private List<String> startupFields;

    private String countryId;
}
