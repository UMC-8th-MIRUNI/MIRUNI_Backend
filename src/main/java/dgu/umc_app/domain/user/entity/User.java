package dgu.umc_app.domain.user.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.time.LocalDateTime;
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

    @Column
    private int executeTime;    //총 수행시간

    @Column
    private int delayTime;  //총 미룬 시간

    @ElementCollection
    private List<Long> delayList = new ArrayList<>(Collections.nCopies(84,0L));     //미룬 시간대

    @ElementCollection
    private List<Long> focusList = new ArrayList<>(Collections.nCopies(84,0L));       //집중한 시간대

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

    /** 월초 롤오버: 카운터/버킷 초기화 */
    public void resetForNewMonth() {
        this.executeTime = 0;
        this.delayTime   = 0;
        this.updatedAt   = java.time.LocalDateTime.now();

        // 리스트가 null일 가능성 방어 (구버전 데이터 대비)
        if (this.delayList == null) this.delayList = new ArrayList<>(Collections.nCopies(84, 0L));
        if (this.focusList == null) this.focusList = new ArrayList<>(Collections.nCopies(84, 0L));

        // 84칸 보장 후 0으로 초기화 (in-place: 변경감지 대상)
        ensureLenAndZero(this.delayList);
        ensureLenAndZero(this.focusList);
    }
    private static void ensureLenAndZero(List<Long> list) {
        final int LEN = 84;
        if (list.size() < LEN) list.addAll(java.util.Collections.nCopies(LEN - list.size(), 0L));
        else if (list.size() > LEN) list.subList(LEN, list.size()).clear();
        java.util.Collections.fill(list, 0L);
    }
}
