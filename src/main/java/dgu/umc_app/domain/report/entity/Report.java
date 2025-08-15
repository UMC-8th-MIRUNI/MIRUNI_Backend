package dgu.umc_app.domain.report.entity;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Table(
        name = "report",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","year","month"})
)
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
    private int year;

    @Column(nullable = false)
    private int month;

    @Builder.Default
    @Column(nullable = false)
    private boolean isOpened = false;

    private LocalDateTime openedAt;

    @Builder.Default
    @Column(nullable = false)
    private int peanutSpent = 0;

    /** 최초 오픈 처리 */
    public void open(int cost, LocalDateTime now) {
        if (this.isOpened) return; // 이미 오픈 시 무시(혹은 예외)
        this.isOpened = true;
        this.openedAt = now;
        this.peanutSpent = cost;
    }

}

