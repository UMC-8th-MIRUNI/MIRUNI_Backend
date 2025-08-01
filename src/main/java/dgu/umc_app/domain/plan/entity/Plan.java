package dgu.umc_app.domain.plan.entity;

import dgu.umc_app.domain.plan.entity.Priority;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일정 아이디 (PK, auto_increment)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자 아이디 (FK)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PlanCategory planCategory;  // 일정 유형(BASIC or AI)

    @Column(nullable = false, length = 50)
    private String title; // 일정 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 일정 내용

    @Column(nullable = false)
    private LocalDateTime deadline; // 마감 기한

    @Column(nullable = false)
    private LocalDateTime executeDate; // 실제 수행 시작날짜

    @Column(nullable = false)
    private boolean isDone; // 완료 체크

    @Column(nullable = false)
    private boolean isDelayed = false;  // 미루기 여부

    @Enumerated(EnumType.STRING)
    private Priority priority;
}