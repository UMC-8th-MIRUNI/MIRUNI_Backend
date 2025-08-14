package dgu.umc_app.domain.plan.entity;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column
    private boolean isDone; // 완료 체크

    @Column
    private boolean isDelayed;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;  // 미완료, 진행중, 중지, 완료

    @Enumerated(EnumType.STRING)
    @Column
    private Priority priority;

    @Column
    private LocalDateTime stoppedAt;

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

    // --Plan 상태 변경 메서드--
    public void updateScheduleStart(LocalDateTime scheduledStart) {this.scheduledStart = scheduledStart;}
    public void updateScheduleEnd(LocalDateTime scheduledEnd) {this.scheduledEnd = scheduledEnd;}
    public void updateStatus(Status status) {this.status = status;}
    public void updateStoppedAt(LocalDateTime stoppedAt) {this.stoppedAt = stoppedAt;}
    public void updateTitle(String title) {this.title = title;}
    public void updateDeadline(LocalDateTime deadline) {this.deadline = deadline;}
    public void updatePriority(Priority priority) {this.priority = priority;}
    public void updateDescription(String description) {this.description = description;}
    public void updateScheduledStart(LocalDateTime scheduledStart) {this.scheduledStart = scheduledStart;}
    public void updateScheduledEnd(LocalDateTime scheduledEnd) {this.scheduledEnd = scheduledEnd;}
    public void touch() {this.updatedAt = LocalDateTime.now();}
}