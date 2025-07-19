package dgu.umc_app.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Configuration
public class FCMConfig {


    @Value("${fcm.key.path:firebase/miruni-9718b-firebase-adminsdk-fbsvc-c1afc8bb2e.json")
    private String fcmKeyPath;

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        try{
            FirebaseApp firebaseApp = getOrCreateFirebaseApp();
            log.info("Firebase 초기화 성공");
            return FirebaseMessaging.getInstance(firebaseApp);
        } catch (Exception e){
            log.error("Firebase 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("Firebase 초기화 실패", e);
        }

    }


    private FirebaseApp getOrCreateFirebaseApp() throws IOException {
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        return firebaseAppList.stream()
                .filter(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                .findFirst()
                .orElseGet(this::initializeFirebaseApp);
    }

    private FirebaseApp initializeFirebaseApp() {
        try (InputStream serviceAccount = new ClassPathResource(fcmKeyPath).getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
