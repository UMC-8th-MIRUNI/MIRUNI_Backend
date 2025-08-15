package dgu.umc_app.domain.report.repository;

import dgu.umc_app.domain.report.entity.LastMonthReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LastMonthReportRepository extends JpaRepository<LastMonthReport, Long> {
    Optional<LastMonthReport> findByUserIdAndYearAndMonth(Long userId, int year, int month);
    boolean existsByUserIdAndYearAndMonth(Long userId, int year, int month);
}
