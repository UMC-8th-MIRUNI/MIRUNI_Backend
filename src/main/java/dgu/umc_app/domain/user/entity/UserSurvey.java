package dgu.umc_app.domain.user.entity;

import dgu.umc_app.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserSurvey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ElementCollection
    @CollectionTable(name = "user_survey_situations", 
                     joinColumns = @JoinColumn(name = "survey_id"))
    @Column(name = "situation_description", nullable = false)
    @Builder.Default
    private Set<String> situationDescriptions = new HashSet<>();

    @Column(nullable = false, name = "delay_level_description")
    private String levelDescription;

    @ElementCollection
    @CollectionTable(name = "user_survey_reasons", 
                     joinColumns = @JoinColumn(name = "survey_id"))
    @Column(name = "reason_description", nullable = false)
    @Builder.Default
    private Set<String> reasonDescriptions = new HashSet<>();
}
