package dgu.umc_app.domain.fcm.repository;

import dgu.umc_app.domain.fcm.entity.FcmToken;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    //사용자별 활성 토큰 조회
    List<FcmToken> findByUserIdAndIsActiveTrueAndNotificationEnabledTrue(Long userId);

    Optional<FcmToken> findByUserAndDeviceId(User user, String deviceId);

}
