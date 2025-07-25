package dgu.umc_app.domain.ai_plan.entity;

import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
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

    @Column(nullable = false, length = 50)
    private String description; // 실행 내용

    @Column(nullable = false)
    private Long expectedDuration; // 예상 실행 시간

    @Column(nullable = false)
    private LocalDate scheduledDate; // 진행 날짜

    @Column(nullable = false)
    private boolean isDone; // 완료 체크

    @Builder
    public AiPlan(Plan plan, Long stepOrder, String description, Long expectedDuration, LocalDate scheduledDate, boolean isDone) {
        this.plan = plan;
        this.stepOrder = stepOrder;
        this.description = description;
        this.expectedDuration = expectedDuration;
        this.scheduledDate = scheduledDate;
        this.isDone = isDone;
    }
}