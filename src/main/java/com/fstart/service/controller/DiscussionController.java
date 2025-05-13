package com.fstart.service.controller;

import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.discussion.*;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.DiscussionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * DiscussionController
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
@RestController
@RequestMapping(value = "fs/api/v1/discussion")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final AccessTokenService accessTokenService;

    public DiscussionController(final DiscussionService discussionService,
                                final AccessTokenService accessTokenService) {
        this.discussionService = discussionService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping(value = "/create-project-discussion", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper createProjectDiscussion(@Valid @RequestBody ProjectDiscussionCreateForm projectDiscussionCreateForm,
                                               HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.createProjectDiscussion(projectDiscussionCreateForm, userId);
    }

    @PostMapping(value = "/create-event-participant-discussion", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper createEventParticipantDiscussion(@Valid @RequestBody EventParticipantDiscussionCreateForm eventParticipantDiscussionForm,
                                                        HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.createEventParticipantDiscussion(eventParticipantDiscussionForm, userId);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper updateDiscussion(@Valid @RequestBody DiscussionUpdateForm discussionUpdateForm,
                                        HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.updateDiscussion(discussionUpdateForm, userId);
    }

    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper deleteDiscussion(@RequestParam(name = "id") @NotNull Long id,
                                        HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.deleteDiscussion(id, userId);
    }

    @GetMapping(value = "/get-discussion-in-project", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper getDiscussionsByIsMemberOfProject(@RequestParam(value = "projectId") String projectId,
                                                         HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.getDiscussionInProjectByUserId(projectId, userId);
    }

    @GetMapping(value = "/all-discussions-by-project", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscussionData> getAllDiscussionsByProject(@RequestParam(value = "projectId") String projectId,
                                                           HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.getDiscussionsByProject(projectId, userId);
    }

    @GetMapping(value = "public/get-project-discussions-in-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscussionData> getProjectDiscussionsInEvent(@RequestParam(value = "projectId") String projectId,
                                                             @RequestParam(value = "eventId") String eventId,
                                                             HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.getProjectDiscussionsInEvent(userId, projectId, eventId);
    }

    @GetMapping(value = "/public-discussions", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper getAllPublicDiscussion() {
        return discussionService.getAllPublicDiscussion();
    }

    @GetMapping(value = "/discussions", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper getDiscussionsByContent(@RequestParam(value = "content") String content,
                                               HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return discussionService.getAllDiscussionsByContent(userId, content);
    }
}
