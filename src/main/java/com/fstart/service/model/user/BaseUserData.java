package com.fstart.service.model.user;

import lombok.*;

/**
 * BaseUserData
 *
 * @author: VuongVT2
 * @since: 2022/01/22
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserData {
    private String id;
    private String avatar;
    private String firstName;
    private String lastName;
}
