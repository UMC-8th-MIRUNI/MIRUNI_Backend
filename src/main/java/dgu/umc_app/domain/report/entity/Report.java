package dgu.umc_app.domain.report.entity;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 정보 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 리포트 대상 연/월
    @Column(nullable = false)
    private int year; // 예: 2025

    @Column(nullable = false)
    private int month; // 예: 8

    // 리포트 오픈 여부 (조건 만족 여부)
    @Column(nullable = false)
    private boolean isOpened;

    // 완료율 (0.0 ~ 100.0)
    @Column(nullable = false)
    private float completionRate;

    // 총 일정 수
    @Column(nullable = false)
    private int totalPlans;

    // 완료 일정 수
    @Column(nullable = false)
    private int completedPlans;

    // 총 수행 시간 (분 단위)
    @Column(nullable = false)
    private int totalExecutionMinutes;

    // 평균 지연 시간 (초 단위)
    @Column(nullable = false)
    private int averageDelaySeconds;

    // 평균 성취도 (0~100)
    @Column(nullable = false)
    private float averageAchievement;

    // 가장 많이 느낀 감정
    @Column(length = 20)
    private String dominantMood;

    // 가장 많이 미뤄진 카테고리
    @Column(length = 50)
    private String mostDelayedCategory;

    // 키워드 요약 (선택)
    @Column(length = 255)
    private String frequentKeywords;

    public void updateCompletionRate(float completionRate) {
        this.completionRate = completionRate;
    }

    public void updateExecutionStats(int totalMinutes, int avgDelaySec) {
        this.totalExecutionMinutes = totalMinutes;
        this.averageDelaySeconds = avgDelaySec;
    }

    public void updateAchievementAndMood(float avgAchievement, String mood) {
        this.averageAchievement = avgAchievement;
        this.dominantMood = mood;
    }
}

