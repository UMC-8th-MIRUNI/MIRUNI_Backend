package dgu.umc_app.domain.user.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

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

    private Integer popupAlarmInterval;

    private Integer bannerAlarmInterval;

    @Column(nullable = false, length = 50)
    private String userPreference;

    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    private ThemeType theme;

    @Column(length = 10)
    private Language language;

    @Builder
    public User(String name, String email, String phoneNumber, String nickname, String password,
                boolean passwordExpired, LocalDateTime lastPasswordChanged, boolean agreedPrivacyPolicy,
                int peanutCount, Integer popupAlarmInterval, Integer bannerAlarmInterval,
                String userPreference, OauthProvider oauthProvider) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.password = password;
        this.passwordExpired = passwordExpired;
        this.lastPasswordChanged = lastPasswordChanged;
        this.agreedPrivacyPolicy = agreedPrivacyPolicy;
        this.peanutCount = peanutCount;
        this.popupAlarmInterval = popupAlarmInterval;
        this.bannerAlarmInterval = bannerAlarmInterval;
        this.userPreference = userPreference;
        this.oauthProvider = oauthProvider;
    }
}
