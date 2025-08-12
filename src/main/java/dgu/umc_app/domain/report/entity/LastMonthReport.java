package dgu.umc_app.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;


@Entity
@Immutable // UPDATE 금지(스냅샷 불변)
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

        @Column(name = "user_id", nullable = false)
        private Long userId;

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

        public static LastMonthReport of(Long userId, int year, int month, String json, LocalDateTime asOf) {
                return LastMonthReport.builder()
                        .userId(userId)
                        .year(year)
                        .month(month)
                        .reportJson(json)
                        .createdAt(asOf)
                        .build();
        }
}
