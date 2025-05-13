package com.fstart.service.model.invitation;

import com.fstart.service.entity.Invitation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * InvitationData
 *
 * @author: VuongVT2
 * @since: 2022/02/15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationData {

    private Invitation invitation;
    private UserInvitationData userInvitationData;

}
