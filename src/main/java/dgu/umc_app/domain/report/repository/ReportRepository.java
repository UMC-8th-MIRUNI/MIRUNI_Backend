package dgu.umc_app.domain.report.repository;

import dgu.umc_app.domain.report.entity.LastMonthReport;
import dgu.umc_app.domain.report.entity.Report;
import dgu.umc_app.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByUserIdAndYearAndMonthAndIsOpenedTrue(Long userId, int year, int month);

    Optional<Report> findByUserIdAndYearAndMonth(Long userId, int year, int month);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      update Report r
         set r.isOpened = true,
             r.openedAt = :now,
             r.peanutSpent = :cost
       where r.user.id = :userId
         and r.year = :year
         and r.month = :month
         and r.isOpened = false
    """)
    int markOpenedIfNotOpened(@Param("userId") Long userId,
                              @Param("year") int year,
                              @Param("month") int month,
                              @Param("now") java.time.LocalDateTime now,
                              @Param("cost") int cost);

    @Query("""
      select distinct r.user.id
        from Report r
       where r.year = :year
         and r.month = :month
         and r.isOpened = true
    """)
    List<Long> findOpenedUserIds(@Param("year") int year, @Param("month") int month);

}