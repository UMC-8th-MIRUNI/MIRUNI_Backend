package dgu.umc_app.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dgu.umc_app.domain.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
