package com.fstart.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * EventDocumentForm
 *
 * @author: VuongVT2
 * @since: 2022/05/29
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantDocumentForm {
    @NotNull
    private Long eventParticipantId;
    private MultipartFile[] documents;
}
