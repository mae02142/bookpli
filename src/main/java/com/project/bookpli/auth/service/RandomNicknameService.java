package com.project.bookpli.auth.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class RandomNicknameService {
    private final List<String> adjectives = Arrays.asList(
            "수줍은", "용감한", "멋진", "귀여운", "활발한", "고요한", "행복한", "차가운", "따뜻한", "우아한"
    );

    private final List<String> nouns = Arrays.asList(
            "도라에몽", "피카츄", "뚱이", "스폰지밥", "미키마우스", "정준하", "윈터", "다람이", "룰루", "영재"
    );

    private final Random random = new Random();

    public String generateNickname() {
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        String noun = nouns.get(random.nextInt(nouns.size()));
        return adjective + " " + noun;
    }
}
