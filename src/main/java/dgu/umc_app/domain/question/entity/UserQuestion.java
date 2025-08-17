package dgu.umc_app.domain.question.entity;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserQuestion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 문의를 작성한 사용자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean agreeToPersonalInfo;        //개인정보 수집 이용 동의

//    @Column(nullable = false)
//    @Builder.Default
//    private Boolean isAnswered = false;  // 답변 여부
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
//    private Answer answer;  // 문의에 대한 답변 (nullable)
}

