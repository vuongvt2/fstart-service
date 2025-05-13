package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.EReportStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

/**
 * Report
 *
 * @author: VuongVT2
 * @since: 2022/04/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "reason", length = 2000)
    private String reason;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EReportStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "report")
    private Collection<UserReport> userReports;

    @JsonIgnore
    @OneToMany(mappedBy = "report")
    private Collection<ProjectReport> projectReports;

    @ManyToOne
    @JoinColumn(name = "violation_id")
    private Violation violation;

}
