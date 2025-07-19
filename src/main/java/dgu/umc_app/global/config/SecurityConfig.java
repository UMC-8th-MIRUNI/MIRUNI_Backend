package dgu.umc_app.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                //CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // session 사용 X
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // form login 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                //http Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // JWT 필터 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                //URL별 권한 설정
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/api/signup",
                                "/api/auth/normal",
                                "/api/auth/google",
                                "/api/auth/kakao",
                                "/api/signup/duplicate",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/schedule"     // jwt 완료 후 지울 예정
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
