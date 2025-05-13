package com.fstart.service.service;

import com.fstart.service.entity.EventParticipant;
import com.fstart.service.enumeration.EEventParticipantStatus;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.event.*;
import com.fstart.service.model.project.DocumentData;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * EventService
 *
 * @author: VuongVT2
 * @since: 2022/04/01
 */
public interface EventService {

    DataWrapper createEvent(EventFormCreate eventFormCreate, String userId);

    DataWrapper updateEvent(EventFormUpdate eventFormUpdate, String userId);

    PagedResponse<BaseEventData> getAllEvent(Pageable pageable, ERole role, String search);

    EventData getEventDetail(String userId, String eventId);

    boolean voteForEventParticipant(Long eventParticipantId, String userId);

    DataWrapper deleteEvent(String id, String userId);

    DataWrapper responseJoinEventRequest(EventParticipantForm eventParticipantForm, String userId);

    DataWrapper requestJoinEvent(String eventId, String projectId, String userId);

    PagedResponse<EventParticipant> getAllJoinEventRequest(Pageable pageable, String userId, String search, EEventParticipantStatus status);

    DataWrapper deleteParticipant(EventParticipantDeleteForm eventParticipantDeleteForm, String userId);

    boolean voteProject(String userId, Long eventParticipantId);

    EventParticipantDetailData getEventParticipantDetail(String userId, Long id);

    List<DocumentData> uploadDocuments(String userId, EventParticipantDocumentForm eventParticipantDocumentForm);

    boolean updateDescription(String userId, EventParticipantUpdateForm eventParticipantUpdateForm);

    boolean unvoteProject(String userId, Long id);

    boolean deleteDocument(String userId, Long eventParticipantId, Long documentId);
}
