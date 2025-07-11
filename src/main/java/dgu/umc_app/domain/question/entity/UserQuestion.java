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

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionCategory category;  // 예: ACCOUNT, PLANNING, ALARM 등

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnswered = false;  // 답변 여부

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private Answer answer;  // 문의에 대한 답변 (nullable)
}

