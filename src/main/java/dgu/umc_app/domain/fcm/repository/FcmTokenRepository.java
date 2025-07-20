package dgu.umc_app.domain.fcm.repository;

import dgu.umc_app.domain.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {


}
