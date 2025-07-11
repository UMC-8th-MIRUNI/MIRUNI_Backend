package dgu.umc_app.domain.question.repository;

import dgu.umc_app.domain.question.entity.UserQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestionRepository extends JpaRepository<UserQuestion, Long> {
}
