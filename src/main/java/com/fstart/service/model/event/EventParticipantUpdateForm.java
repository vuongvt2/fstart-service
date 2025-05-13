package com.fstart.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantUpdateForm {

    @NotNull
    private Long eventParticipantId;

    @NotBlank
    private String description;
}
