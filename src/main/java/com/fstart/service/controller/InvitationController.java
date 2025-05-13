package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.enumeration.EUserInvitationType;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.invitation.InvitationData;
import com.fstart.service.model.invitation.ProjectInvitationForm;
import com.fstart.service.model.invitation.RequestForm;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.InvitationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * InvitationController
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Validated
@RestController
@RequestMapping(value = "/fs/api/v1/invitation")
public class InvitationController {

    private final AccessTokenService accessTokenService;
    private final InvitationService invitationService;

    public InvitationController(final AccessTokenService accessTokenService,
                                final InvitationService invitationService) {
        this.accessTokenService = accessTokenService;
        this.invitationService = invitationService;
    }

    @PostMapping(value = "/send-invitation", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper sendInvitation(@RequestBody @Valid ProjectInvitationForm projectInvitationForm,
                                      HttpServletRequest request) {
        String senderId = accessTokenService.getUserID(request);
        return invitationService.sendInvitation(senderId, projectInvitationForm);
    }

    @PostMapping(value = "/send-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper sendRequest(@RequestBody @Valid RequestForm requestForm,
                                   HttpServletRequest request) {
        String senderId = accessTokenService.getUserID(request);
        return invitationService.sendRequest(senderId, requestForm);
    }

    @GetMapping(value = "/accept-invitation", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper acceptInvitation(@RequestParam(value = "id") @NotNull Long id,
                                        HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return invitationService.acceptInvitation(id, userId);
    }

    @GetMapping(value = "/accept-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper acceptRequest(@RequestParam(value = "id") @NotNull Long id,
                                     HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return invitationService.acceptRequest(id, userId);
    }

    @GetMapping(value = "/refuse-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper refuseRequest(@RequestParam(value = "id") @NotNull Long id,
                                     HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return invitationService.refuseRequest(id, userId);
    }

    @GetMapping(value = "/refuse-invitation", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper refuseInvitation(@RequestParam(value = "id") @NotNull Long id,
                                        HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return invitationService.refuseInvitation(id, userId);
    }

    @GetMapping(value = "/all/sent-invitation-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<InvitationData> getAllSentInvitationsAndRequests(@RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                                          @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                                          HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        Pageable pageable = PageRequest.of(page - 1, size);
        return invitationService.getAllInvitationsAndRequestsByType(userId, EUserInvitationType.SENDER, pageable);
    }

    @GetMapping(value = "/all/received-invitation-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<InvitationData> getAllReceivedInvitationsAndRequests(@RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                                              @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        Pageable pageable = PageRequest.of(page - 1, size);
        return invitationService.getAllInvitationsAndRequestsByType(userId, EUserInvitationType.RECEIVER, pageable);
    }

    @GetMapping(value = "/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper cancelRequestOrInvitation(@RequestParam(value = "id") @NotNull Long id,
                                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return invitationService.cancelRequestOrInvitation(id, userId);
    }

}
