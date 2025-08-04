package dgu.umc_app.domain.fcm.service;

import dgu.umc_app.domain.fcm.entity.FcmToken;
import dgu.umc_app.domain.fcm.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTokenQueryService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional(readOnly = true)
    public List<String> getActiveTokenByUserId(Long userId){
        return fcmTokenRepository.findByUserIdAndIsActiveTrueAndNotificationEnabledTrue(userId)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }



}
