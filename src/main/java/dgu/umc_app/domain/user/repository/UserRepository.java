package dgu.umc_app.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      update User u
         set u.peanutCount = u.peanutCount - :cost
       where u.id = :userId
         and u.peanutCount >= :cost
    """)
    int trySpendPeanuts(Long userId, int cost);
}
