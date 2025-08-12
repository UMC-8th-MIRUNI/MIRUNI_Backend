package dgu.umc_app.domain.plan.entity;

import dgu.umc_app.domain.plan.entity.Priority;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column(nullable = false, length = 50)
    private String title; // 일정 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 일정 내용

    @Column(nullable = false)
    private LocalDateTime deadline; // 마감 기한

    @Column(nullable = false)
    private LocalDateTime scheduledStart; // 수행시작 예정 날짜&시간(ex: 2025-05-01T21:00:00)

    @Column(nullable = false)
    private LocalDateTime scheduledEnd; // 수행종료 예정 날짜&시간(ex: 2025-05-01T22:00:00)

    @Column(nullable = false)
    private boolean isDone; // 완료 체크

    @Column(nullable = false)
    private boolean isDelayed = false;  // 미루기 여부

    @Enumerated(EnumType.STRING)
    @Column
    private Priority priority;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<AiPlan> aiPlans = new ArrayList<>();

    public void addAiPlan(AiPlan ai) {
        if (ai == null) return;
        ai.setPlan(this);
        aiPlans.add(ai);
    }

    public void removeAiPlan(AiPlan ai) {
        if (ai == null) return;
        aiPlans.remove(ai);
        ai.setPlan(null);
    }
}