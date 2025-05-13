package com.fstart.service.service;

import com.fstart.service.enumeration.EUserInvitationType;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.invitation.InvitationData;
import com.fstart.service.model.invitation.ProjectInvitationForm;
import com.fstart.service.model.invitation.RequestForm;
import org.springframework.data.domain.Pageable;

/**
 * InvitationService
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
public interface InvitationService {

    DataWrapper sendInvitation(String senderId, ProjectInvitationForm projectInvitationForm);

    DataWrapper sendRequest(String senderId, RequestForm requestForm);

    DataWrapper acceptInvitation(Long id, String userId);

    DataWrapper acceptRequest(Long id, String userId);

    DataWrapper refuseRequest(Long id, String userId);

    DataWrapper refuseInvitation(Long id, String userId);

    PagedResponse<InvitationData> getAllInvitationsAndRequestsByType(String userId, EUserInvitationType type, Pageable pageable);

    DataWrapper cancelRequestOrInvitation(Long id, String userId);
}
