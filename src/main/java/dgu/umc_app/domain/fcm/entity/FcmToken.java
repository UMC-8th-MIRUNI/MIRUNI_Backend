package dgu.umc_app.domain.fcm.entity;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FcmToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500, unique = true)
    private String token; //FCM 토큰

    @Column(length = 100)
    private String deviceId; // 멀티 디바이스 환경 대비

    @Column(nullable = false)
    private boolean isActive = true; //토큰 유효성 체크

    @Column(nullable = false)
    private boolean notificationEnabled = true; //해당 디바이스의 알림 활성화 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //한 사용자가 여러기기 사용가능 고려

    // == fcmToken 비활성화 및 활성화 메서드 ==/
    public void updateNotificationEnabled (boolean notificationEnabled) {
        this.isActive = notificationEnabled;
    }
}
