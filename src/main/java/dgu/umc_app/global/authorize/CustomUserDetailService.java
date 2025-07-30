package dgu.umc_app.global.authorize;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.global.exception.BaseException;
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
            .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
        
        return new CustomUserDetails(user);
    }
    
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
        
        return new CustomUserDetails(user);
    }
} 