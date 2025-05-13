package com.fstart.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * EventParticipantForm
 *
 * @author VuongVT2
 * @since 2022/04/01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantDeleteForm implements Serializable {

    @NotBlank
    private String eventId;

    @NotBlank
    @Length(max = 10)
    private String projectId;

}
