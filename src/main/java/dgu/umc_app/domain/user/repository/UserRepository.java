package dgu.umc_app.domain.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import dgu.umc_app.domain.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    // delayList / focusList가 @ElementCollection이면 fetch join 대신 EntityGraph가 깔끔
    @EntityGraph(attributePaths = { "delayList", "focusList" })
    Optional<User> findWithSlotsById(Long id);
}
