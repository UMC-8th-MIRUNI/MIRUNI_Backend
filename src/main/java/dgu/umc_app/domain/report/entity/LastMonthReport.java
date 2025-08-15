package dgu.umc_app.domain.report.entity;

import dgu.umc_app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@Immutable // UPDATE 금지
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "last_month_report",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","year","month"}))
public class LastMonthReport {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = LAZY, optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false)
        private int year;

        @Column(nullable = false)
        private int month;

        /**
         * ReportResponse JSON 그대로 저장
         */
        @Lob
        @Column(columnDefinition = "json", nullable = false)
        private String reportJson;

        @Column(nullable = false)
        private LocalDateTime createdAt;

        public static LastMonthReport of(User user, int year, int month, String json, LocalDateTime asOf) {
                return LastMonthReport.builder()
                        .user(user)
                        .year(year)
                        .month(month)
                        .reportJson(json)
                        .createdAt(asOf)
                        .build();
        }
}
