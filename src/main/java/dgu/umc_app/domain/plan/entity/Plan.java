package dgu.umc_app.domain.plan.entity;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private boolean isDone; // 완료 체크

    @Builder(toBuilder = true)
    public Plan(User user, String title, String description, LocalDateTime deadline, boolean isDone) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isDone = isDone;
    }

}