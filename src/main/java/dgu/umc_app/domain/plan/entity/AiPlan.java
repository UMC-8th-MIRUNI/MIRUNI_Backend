package dgu.umc_app.domain.plan.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // AI가 짜준 일정 계획의 고유 식별자 (PK, auto_increment)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Plan plan; // plan 테이블의 일정 아이디와 연결되는 외래 키

    @Column(nullable = false)
    private Long stepOrder; // 실행 순서

    @Enumerated(EnumType.STRING)
    @Column
    private Priority priority;  // 우선순위

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;  // 작업 유형

    @Column(nullable = false)
    private String taskRange;   // 작업 범위

    @Column(nullable = false, length = 50)
    private String description; // 실행 내용

    @Column(nullable = false)
    private Long expectedDuration; // 예상 소요 시간

    @Column(nullable = false)
    private LocalDateTime scheduledStart; // 수행시작 예정 날짜&시간(ex: 2025-05-01T21:00:00)

    @Column(nullable = false)
    private LocalDateTime scheduledEnd; // 수행끝 예정 날짜&시간(ex: 2025-05-01T22:00:00)

    @Column(nullable = false)
    private boolean isDone; // 완료 여부

    @Column(nullable = false)
    private boolean isDelayed = false;  // 미루기 여부

    private LocalDateTime tempTime;

    public LocalDateTime getTaskTime() {
        return LocalDateTime.of(scheduledStart.toLocalDate(), scheduledStart.toLocalTime());
    }
}