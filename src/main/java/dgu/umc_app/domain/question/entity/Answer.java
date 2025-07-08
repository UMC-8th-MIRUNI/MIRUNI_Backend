package dgu.umc_app.domain.question.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "question_id")
    private UserQuestion question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

}
