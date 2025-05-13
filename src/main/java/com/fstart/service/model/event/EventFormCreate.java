package com.fstart.service.model.event;

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
 * EventForm
 *
 * @author VuongVT2
 * @since 2022/04/01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFormCreate implements Serializable {


    @NotBlank
    private String description;

    @NotBlank
    @Length(max = 14)
    private String endTime;

    @NotBlank
    @Length(max = 14)
    private String startTime;

    @NotBlank
    @Length(max = 150)
    private String title;

    @NotNull
    private MultipartFile banner;
}
