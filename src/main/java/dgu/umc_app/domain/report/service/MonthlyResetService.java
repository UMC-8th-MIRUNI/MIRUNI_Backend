package dgu.umc_app.domain.report.service;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonthlyResetService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetBatch(List<Long> ids) {
        var users = userRepository.findAllById(ids);
        for (User u : users) {
            u.resetForNewMonth();
        }
    }
}
