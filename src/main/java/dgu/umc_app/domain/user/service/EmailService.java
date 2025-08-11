package dgu.umc_app.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.domain.user.exception.UserErrorCode;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendPasswordResetCode(String email, String code) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom("noreply@miruni.com", "Miruni");
            helper.setTo(email);
            helper.setSubject("🔐 Miruni 비밀번호 재설정 인증 코드");
            
            String htmlContent = loadEmailTemplate(code);
            helper.setText(htmlContent, true);
            
            // 이메일 전송
            javaMailSender.send(mimeMessage);
            log.info("비밀번호 재설정 인증 코드를 {}로 성공적으로 전송했습니다: {}", email, code);
            
        } catch (Exception e) {
            log.error("이메일 전송 실패 - 수신자: {}, 오류: {}", email, e.getMessage(), e);
            throw BaseException.type(UserErrorCode.EMAIL_SEND_FAILED);
        }
    }
    
    private String loadEmailTemplate(String code) {
        try {
            Context context = new Context();
            context.setVariable("code", code);
            
            return templateEngine.process("email", context);
        } catch (Exception e) {
            log.error("이메일 템플릿 로드 실패: {}", e.getMessage(), e);
            return createSimpleEmailTemplate(code);
        }
    }
    
    private String createSimpleEmailTemplate(String code) {
        return String.format("""
            <!DOCTYPE html>
            <html>
                <body>
                    <div style="font-family: Arial; max-width: 600px; margin: 0 auto;">
                        <h2 style="color: #333;">🔐 Miruni 비밀번호 재설정</h2>
                        <p>안녕하세요! 비밀번호 재설정을 위한 인증 코드입니다.</p>
                        <div style="background: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0;">
                            <h1 style="color: #007bff; font-size: 32px; margin: 0;">%s</h1>
                        </div>
                        <p><strong>주의:</strong> 이 코드는 10분 후 만료됩니다.</p>
                        <p>본인이 요청하지 않은 경우 이 이메일을 무시하세요.</p>
                    </div>
                </body>
            </html>
            """, code);
    }
}
