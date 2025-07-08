package dgu.umc_app.domain.user.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Language {
    KO("ko"),
    EN("en");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String buildPrompt(String task) {
        return task;
    }

    public static Language fromCode(String code) {  // enum 형태로 변환
        return Arrays.stream(values())
                .filter(lang -> lang.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(KO);
    }
}