package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.entity.EventParticipant;
import com.fstart.service.enumeration.EEventParticipantStatus;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.event.*;
import com.fstart.service.model.project.DocumentData;
import com.fstart.service.model.project.ProjectDocumentForm;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.EventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * CommentController
 *
 * @author: VuongVT2
 * @since: 2022/04/01
 */
@RestController
@RequestMapping(value = "fs/api/v1/event")
public class EventController {
    private final EventService eventService;
    private final AccessTokenService accessTokenService;

    public EventController(EventService eventService, AccessTokenService accessTokenService) {
        this.eventService = eventService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping(value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataWrapper createEvent(@Valid EventFormCreate eventFormCreate,
                                   HttpServletRequest request) {
        //Admin
        String userId = accessTokenService.getUserID(request);
        return eventService.createEvent(eventFormCreate, userId);
    }

    @PostMapping(value = "/update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataWrapper updateEvent(EventFormUpdate eventFormUpdate,
                                   HttpServletRequest request) {
        //Admin
        String userId = accessTokenService.getUserID(request);
        return eventService.updateEvent(eventFormUpdate, userId);
    }

    @PostMapping(value = "/vote-for-event-participant", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean voteForEventParticipant(@RequestParam Long eventParticipantId,
                                           HttpServletRequest request) {
        //Admin
        String userId = accessTokenService.getUserID(request);
        return eventService.voteForEventParticipant(eventParticipantId, userId);
    }

    @GetMapping(value = "public/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<BaseEventData> getAllEvent(@RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                    @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                    @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return eventService.getAllEvent(pageable, null, search);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<BaseEventData> getAllEventForAdmin(@RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                            @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                            @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        String roleId = accessTokenService.getUserRole(request);
        ERole role = StringUtils.hasLength(roleId) ? ERole.valueOf(roleId) : null;
        return eventService.getAllEvent(pageable, role, search);
    }

    @GetMapping(value = "public/event", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventData getEventDetail(@RequestParam(value = "id") @NotBlank String id,
                                    HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getEventDetail(userId, id);
    }

    @GetMapping(value = "event-participant", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventParticipantDetailData getEventParticipantDetail(@RequestParam(value = "id") @NotNull Long id,
                                                                HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getEventParticipantDetail(userId, id);
    }


    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper deleteEvent(@RequestParam("id") String id,
                                   HttpServletRequest request) {
        //Admin
        String userId = accessTokenService.getUserID(request);
        return eventService.deleteEvent(id, userId);
    }

    @PostMapping(value = "delete-participant", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper deleteParticipant(@RequestBody EventParticipantDeleteForm eventParticipantDeleteForm,
                                         HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.deleteParticipant(eventParticipantDeleteForm, userId);
    }

    @PostMapping(value = "/response-join-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper acceptOrRejectJoinEventRequest(@RequestBody @Valid EventParticipantForm eventParticipantForm,
                                                      HttpServletRequest request) {
        //Admin
        String userId = accessTokenService.getUserID(request);
        return eventService.responseJoinEventRequest(eventParticipantForm, userId);
    }


    @GetMapping(value = "/request-join-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper requestJoinEvent(@RequestParam(name = "eventId") String eventId,
                                        @RequestParam(name = "projectId") String projectId,
                                        HttpServletRequest request) {
        //Project Owner
        String userId = accessTokenService.getUserID(request);
        return eventService.requestJoinEvent(eventId, projectId, userId);
    }

    @GetMapping(value = "/get-all-join-event-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<EventParticipant> getAllJoinEventRequest(@RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                                  @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                                  @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                                  @RequestParam(name = "status", defaultValue = "NEW") EEventParticipantStatus status,
                                                                  HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        //Admin
        String userId = accessTokenService.getUserID(request);
        return eventService.getAllJoinEventRequest(pageable, userId, search, status);
    }

    @GetMapping(value = "/vote", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean voteProject(@RequestParam(name = "eventParticipantId") @NotNull Long eventParticipantId,
                               HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.voteProject(userId, eventParticipantId);
    }

    @GetMapping(value = "/unvote", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean unvoteProject(@RequestParam(name = "eventParticipantId") @NotNull Long id,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.unvoteProject(userId, id);
    }

    @PostMapping(value = "/upload-document", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<DocumentData> uploadDocuments(@Valid EventParticipantDocumentForm eventParticipantDocumentForm,
                                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.uploadDocuments(userId, eventParticipantDocumentForm);
    }

    @PostMapping(value = "/update-description", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateDescription(@Valid @RequestBody EventParticipantUpdateForm eventParticipantUpdateForm,
                                     HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.updateDescription(userId, eventParticipantUpdateForm);
    }

    @GetMapping(value = "/delete-document", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteDocument(@RequestParam(value = "eventParticipantId") @NotBlank Long eventParticipantId,
                                  @RequestParam(value = "documentId") @NotNull Long documentId,
                                  HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.deleteDocument(userId, eventParticipantId, documentId);
    }
}
