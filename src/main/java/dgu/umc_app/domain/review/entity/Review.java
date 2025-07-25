package dgu.umc_app.domain.review.entity;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 회고 아이디 (PK, auto_increment)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_plan_id", nullable = false)
    private AiPlan aiPlan; // AI 계획 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan; // 일정 외래키 (id2에 해당)

    @Column(nullable = false, length = 50)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Mood mood;

    @Column(nullable = false)
    private byte achievement; // 성취도

    @Column(nullable = false)
    private byte satisfaction; // 만족도

    @Column(nullable = false, length = 255)
    private String memo; // 2줄 정도의 회고 메모

    @Builder
    public Review(AiPlan aiPlan, Plan plan, String title, Mood mood, byte achievement, byte satisfaction, String memo) {
        this.aiPlan = aiPlan;
        this.plan = plan;
        this.title = title;
        this.mood = mood;
        this.achievement = achievement;
        this.satisfaction = satisfaction;
        this.memo = memo;
    }
}
