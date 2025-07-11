package dgu.umc_app.domain.user.authorize;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.CommonErrorCode;
import dgu.umc_app.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(CommonErrorCode.USER_NOT_FOUND));
        
            return new CustomUserDetails(user);
    }
} 