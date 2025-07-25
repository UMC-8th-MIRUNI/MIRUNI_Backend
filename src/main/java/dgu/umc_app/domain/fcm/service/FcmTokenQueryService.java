package dgu.umc_app.domain.fcm.service;

import dgu.umc_app.domain.fcm.entity.FcmToken;
import dgu.umc_app.domain.fcm.repository.FcmTokenRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.authorize.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTokenQueryService {


    private final FcmTokenRepository fcmTokenRepository;

    @Transactional(readOnly = true)
    public List<String> getActiveTokenByUser(){
        User user = getCurrentUser();

        return fcmTokenRepository.findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}
