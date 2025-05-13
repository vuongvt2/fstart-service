package com.fstart.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fstart.service.enumeration.EUserStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * User
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
@Table(name = "fs_user")
public class User implements Serializable {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "pwd", length = 60)
    private String pwd;

    @Column(name = "first_name", length = 150, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 150, nullable = false)
    private String lastName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "phone_number", length = 12)
    private String phoneNumber;

    @Column(name = "avatar", length = 1024)
    private String avatar;

    @Column(name = "facebook_link", length = 150)
    private String facebookLink;

    @Column(name = "github_link", length = 150)
    private String githubLink;

    @Column(name = "linkedin_link", length = 150)
    private String linkedinLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EUserStatus status;

    @Column(name = "address")
    private String address;

    @Column(name = "created_at", length = 14, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 14, nullable = false)
    private String updatedAt;

    @Column(name = "reason", length = 2000)
    private String reason;

    // Keys
    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<Experience> experiences;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<ProjectTeamMember> projectTeamMembers;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<Discussion> discussions;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<News> news;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<UserInvitation> userInvitations;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<UserPosition> userPositions;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<UserField> userFields;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<UserSkill> userSkills;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<UserReport> userReports;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<ProjectReport> projectReports;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<UserVerification> userVerifications;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Collection<Vote> votes;
}
