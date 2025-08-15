package dgu.umc_app.domain.user.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Column(name = "delay_time")
    private int delayTime = 0; // 총 미룬 시간

    @Column(name = "execute_time")
    private int executeTime = 0;  // 총 실행시간

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_delay_list",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @OrderColumn(name = "slot_order") // 0..83 순서 고정
    @Column
    private List<Long> delayList;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_focus_list",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @OrderColumn(name = "slot_order")
    @Column
    private List<Long> focusList;

    @PrePersist
    private void initSlotsOnCreate() {
        if (delayList == null || delayList.isEmpty()) {
            delayList = new ArrayList<>(Collections.nCopies(84, 0L));
        }
        if (focusList == null || focusList.isEmpty()) {
            focusList = new ArrayList<>(Collections.nCopies(84, 0L));
        }
    }

    // userPreference 필드 제거

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

    // Survey 관련 비트마스크 컬럼들
    @Column(name = "delay_situations_mask")
    private Long delaySituationsMask;

    @Enumerated(EnumType.STRING)
    @Column(name = "delay_level")
    private DelayLevel delayLevel;

    @Column(name = "delay_reasons_mask")
    private Long delayReasonsMask;

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

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.lastPasswordChanged = LocalDateTime.now();
        this.passwordExpired = false;
    }

    public boolean isSocialUser() {
        return this.oauthProvider != null;
    }

    public boolean hasPassword() {
        return this.password != null && !this.password.isEmpty();
    }
  
    public void updateProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void completeSurvey() {
        this.surveyStatus = SurveyStatus.COMPLETED;
        this.status = Status.ACTIVE;
    }

    public boolean isSurveyCompleted() {
        return this.surveyStatus == SurveyStatus.COMPLETED;
    }

    // Survey 관련 메서드들
    public void updateSurveyInfo(Set<DelaySituation> situations, DelayLevel level, Set<DelayReason> reasons) {
        this.delaySituationsMask = DelaySituation.createMask(situations);
        this.delayLevel = level;
        this.delayReasonsMask = DelayReason.createMask(reasons);
        this.surveyStatus = SurveyStatus.COMPLETED;
        this.status = Status.ACTIVE;
    }

    public Set<DelaySituation> getDelaySituations() {
        return this.delaySituationsMask != null ? DelaySituation.fromMask(this.delaySituationsMask) : new HashSet<>();
    }

    public Set<DelayReason> getDelayReasons() {
        return this.delayReasonsMask != null ? DelayReason.fromMask(this.delayReasonsMask) : new HashSet<>();
    }

    public DelayLevel getDelayLevel() {
        return this.delayLevel;
    }

    public void updateUser(String name, String email, String birthday, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }
    public void updateDelayTime(int delayTime) {this.delayTime = delayTime;}
    public void updateExecuteTime(int executeTime) {this.executeTime = executeTime;}
    public void updateDelayList(List<Long> delayTimeSlots) {this.delayList = delayTimeSlots;}
    public void updateFocusList(List<Long> focusSlots) {this.focusList = focusSlots;}
    public void updatePeanutCount(int peanutCount) {this.peanutCount = peanutCount;}
}
