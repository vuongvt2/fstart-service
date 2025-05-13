package com.fstart.service.model.invitation;

import lombok.*;

/**
 * UserInvitationData
 *
 * @author: VuongVT2
 * @since: 2022/03/30
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInvitationData {
    private String type;
    private String userId;
    private String fullName;
}
