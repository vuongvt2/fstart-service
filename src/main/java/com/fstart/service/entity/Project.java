package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.EProjectPrivacy;
import com.fstart.service.enumeration.EProjectStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Project
 *
 * @author: VuongVT2
 * @since: 2021/10/24
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fs_project")
public class Project implements Serializable {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "logo", length = 1024, nullable = false)
    private String logo;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "sub_title")
    private String subTitle;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EProjectStatus status;

    @Column(name = "reason", length = 2000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", length = 20, nullable = false)
    private EProjectPrivacy privacy;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    @Column(name = "is_call_for_investment", columnDefinition = "boolean default false")
    private boolean callForInvestment;

    // Keys
    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<Document> documents;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<Invitation> invitations;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<ProjectTeamMember> projectTeamMembers;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<EventParticipant> eventParticipants;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<Discussion> discussions;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<ProjectTechnology> projectTechnologies;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<ProjectField> projectFields;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<ProjectReport> projectReports;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Collection<ProjectPosition> projectPositions;
}