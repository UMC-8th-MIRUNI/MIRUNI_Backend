package dgu.umc_app.domain.user.repository;

import dgu.umc_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
