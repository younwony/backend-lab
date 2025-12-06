package com.example.webapp.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;

/**
 * 계절 감지 및 관련 정보를 제공하는 서비스
 */
@Service
public class SeasonService {

    /**
     * 계절 열거형
     */
    public enum Season {
        SPRING("봄", "spring"),
        SUMMER("여름", "summer"),
        AUTUMN("가을", "autumn"),
        WINTER("겨울", "winter");

        private final String koreanName;
        private final String englishName;

        Season(String koreanName, String englishName) {
            this.koreanName = koreanName;
            this.englishName = englishName;
        }

        public String getKoreanName() {
            return koreanName;
        }

        public String getEnglishName() {
            return englishName;
        }
    }

    /**
     * 현재 날짜 기준으로 계절을 반환
     * 봄: 3월, 4월, 5월
     * 여름: 6월, 7월, 8월
     * 가을: 9월, 10월, 11월
     * 겨울: 12월, 1월, 2월
     */
    public Season getCurrentSeason() {
        return getSeasonByDate(LocalDate.now());
    }

    /**
     * 특정 날짜의 계절을 반환
     */
    public Season getSeasonByDate(LocalDate date) {
        Month month = date.getMonth();

        return switch (month) {
            case MARCH, APRIL, MAY -> Season.SPRING;
            case JUNE, JULY, AUGUST -> Season.SUMMER;
            case SEPTEMBER, OCTOBER, NOVEMBER -> Season.AUTUMN;
            case DECEMBER, JANUARY, FEBRUARY -> Season.WINTER;
        };
    }

    /**
     * 현재 계절의 한글 이름 반환
     */
    public String getCurrentSeasonKoreanName() {
        return getCurrentSeason().getKoreanName();
    }

    /**
     * 현재 계절의 영문 이름 반환 (CSS 클래스명으로 사용)
     */
    public String getCurrentSeasonEnglishName() {
        return getCurrentSeason().getEnglishName();
    }
}
