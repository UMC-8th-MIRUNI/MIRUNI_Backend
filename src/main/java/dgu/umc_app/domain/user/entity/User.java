package dgu.umc_app.domain.user.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
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
    private int peanutCount = 0;

    @ElementCollection
    @CollectionTable(
            name = "user_delay_times",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "delay_time")
    private List<Long> delayTimes = Arrays.asList(0L, 0L); // index 0 : 이번 달, index 1 : 저번 달

    @Column
    private YearMonth delayBaseMonth; // 미룰 때 index 0이 가리키는 달

    @ElementCollection
    @CollectionTable(
            name = "user_execute_times",  // 별도 테이블 이름
            joinColumns = @JoinColumn(name = "user_id") // User 엔티티와 FK 연결
    )
    @Column(name = "execute_time") // 컬럼명
    private List<Long> executeTimes = Arrays.asList(0L, 0L);

    @Column
    private YearMonth executeBaseMonth; // 실행할 때 index 0이 가리키는 달

    @Column(nullable = false, length = 50)
    private String userPreference;

    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    private ThemeType theme;

    @Column(length = 10)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

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

    public void addDelayTime(long delayMinutes, LocalDateTime stoppedAt) {
        if (delayMinutes <= 0) return;

        YearMonth nowMonth = YearMonth.from(stoppedAt);
        if (delayBaseMonth == null) {
            delayBaseMonth = nowMonth;
            delayTimes.set(0, delayTimes.get(0) + delayMinutes);
            return;
        }

        if (nowMonth.equals(delayBaseMonth)) {
            // 이번 달
            delayTimes.set(0, delayTimes.get(0) + delayMinutes);
            return;
        }

        if (nowMonth.equals(delayBaseMonth.minusMonths(1))) {
            // 저번 달
            delayTimes.set(1, delayTimes.get(1) + delayMinutes);
            return;
        }

        if (nowMonth.isAfter(delayBaseMonth)) {
            // 미래 달로 넘어감 → (한 달 후) 혹은 (여러 달 후)
            long currentMonthValue = delayMinutes;
            long lastMonthValue = 0L;

            if (nowMonth.equals(delayBaseMonth.plusMonths(1))) {
                // 한 달만 이동 → 기존 달 값을 저번 달로 이월
                lastMonthValue = delayTimes.get(0);
            }
            delayTimes.set(0, currentMonthValue);
            delayTimes.set(1, lastMonthValue);
            delayBaseMonth = nowMonth;
        }
    }
    public void addExecuteTime(long executeMinutes, LocalDateTime stoppedAt) {
            if (executeMinutes <= 0) return;

            YearMonth nowMonth = YearMonth.from(stoppedAt);
            if (executeBaseMonth == null) {
                executeBaseMonth = nowMonth;
                executeTimes.set(0, executeTimes.get(0) + executeMinutes);
                return;
            }

            if (nowMonth.equals(executeBaseMonth)) {
                // 이번 달
                executeTimes.set(0, executeTimes.get(0) + executeMinutes);
                return;
            }

            if (nowMonth.equals(executeBaseMonth.minusMonths(1))) {
                // 저번 달
                executeTimes.set(1, executeTimes.get(1) + executeMinutes);
                return;
            }

            if (nowMonth.isAfter(executeBaseMonth)) {
                // 미래 달로 넘어감 → (한 달 후) 혹은 (여러 달 후)
                long currentMonthValue = executeMinutes;
                long lastMonthValue = 0L;

                if (nowMonth.equals(executeBaseMonth.plusMonths(1))) {
                    // 한 달만 이동 → 기존 이번 달 값을 저번 달로 이월
                    lastMonthValue = executeTimes.get(0);
                }
                executeTimes.set(0, currentMonthValue); 
                executeTimes.set(1, lastMonthValue);
                executeBaseMonth = nowMonth;
            }
        }


}
