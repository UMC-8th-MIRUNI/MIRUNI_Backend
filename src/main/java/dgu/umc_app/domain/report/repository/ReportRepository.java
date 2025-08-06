package dgu.umc_app.domain.report.repository;

import dgu.umc_app.domain.report.entity.Report;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByUserIdAndYearAndMonthAndIsOpenedTrue(Long userId, int year, int month);
}
