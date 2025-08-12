package dgu.umc_app.domain.user.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = true, length = 20)
    private String birthday;

    @Column(nullable = false, length = 255)
    private String phoneNumber;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean passwordExpired;

    private LocalDateTime lastPasswordChanged;

    @Column(nullable = false)
    private boolean agreedPrivacyPolicy;

    @Column(nullable = false)
    @Builder.Default
    private int peanutCount = 0;

    // userPreference 필드 제거됨 - UserSurvey 엔티티로 대체

    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    private ThemeType theme;

    @Column(length = 10)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SurveyStatus surveyStatus = SurveyStatus.NOT_COMPLETED;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProfileImage profileImage = ProfileImage.GREEN;

    // == User 상태 변경 메서드들 == //
    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void setPending() {
        this.status = Status.PENDING;
    }

    public void delete() {
        this.status = Status.DELETED;
    }

    public void restore() {
        this.status = Status.ACTIVE;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public boolean isPending() {
        return this.status == Status.PENDING;
    }

    public boolean isDeleted() {
        return this.status == Status.DELETED;
    }

    public void updateGoogleSignUpInfo(String name, String birthday, String phoneNumber, Boolean agreedPrivacyPolicy, String nickname) {
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.agreedPrivacyPolicy = agreedPrivacyPolicy != null ? agreedPrivacyPolicy : false;
        this.nickname = nickname;
    }

    public void updateKakaoSignUpInfo(String name, String birthday, String phoneNumber, Boolean agreedPrivacyPolicy, String nickname) {
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.agreedPrivacyPolicy = agreedPrivacyPolicy != null ? agreedPrivacyPolicy : false;
        this.nickname = nickname;
    }

    public void updateProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public void completeSurvey() {
        this.surveyStatus = SurveyStatus.COMPLETED;
        this.status = Status.ACTIVE;
    }

    public boolean isSurveyCompleted() {
        return this.surveyStatus == SurveyStatus.COMPLETED;
    }
}
