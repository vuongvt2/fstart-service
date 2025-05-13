package com.fstart.service.model.user;


import lombok.*;

/**
 * UserReportData
 *
 * @author: VuongVT2
 * @since: 2022/04/1
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportData {

    private String id;
    private String avatar;
    private String firstName;
    private String lastName;
    private String violation;
    private String reason;
}
