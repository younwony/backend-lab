package com.example.webapp.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SeasonService 테스트")
class SeasonServiceTest {

    private final SeasonService seasonService = new SeasonService();

    @ParameterizedTest
    @DisplayName("월별 계절 반환 테스트")
    @CsvSource({
            "2024-03-15, SPRING",
            "2024-04-15, SPRING",
            "2024-05-15, SPRING",
            "2024-06-15, SUMMER",
            "2024-07-15, SUMMER",
            "2024-08-15, SUMMER",
            "2024-09-15, AUTUMN",
            "2024-10-15, AUTUMN",
            "2024-11-15, AUTUMN",
            "2024-12-15, WINTER",
            "2024-01-15, WINTER",
            "2024-02-15, WINTER"
    })
    void getSeasonByDate_ReturnsCorrectSeason(String dateStr, String expectedSeason) {
        // given
        LocalDate date = LocalDate.parse(dateStr);

        // when
        SeasonService.Season season = seasonService.getSeasonByDate(date);

        // then
        assertThat(season.name()).isEqualTo(expectedSeason);
    }

    @Test
    @DisplayName("봄 계절의 한글 이름은 '봄'이어야 한다")
    void spring_KoreanName_IsBom() {
        // given
        LocalDate springDate = LocalDate.of(2024, 4, 1);

        // when
        SeasonService.Season season = seasonService.getSeasonByDate(springDate);

        // then
        assertThat(season.getKoreanName()).isEqualTo("봄");
    }

    @Test
    @DisplayName("봄 계절의 영문 이름은 'spring'이어야 한다")
    void spring_EnglishName_IsSpring() {
        // given
        LocalDate springDate = LocalDate.of(2024, 4, 1);

        // when
        SeasonService.Season season = seasonService.getSeasonByDate(springDate);

        // then
        assertThat(season.getEnglishName()).isEqualTo("spring");
    }

    @Test
    @DisplayName("현재 계절을 반환해야 한다")
    void getCurrentSeason_ReturnsCurrentSeason() {
        // when
        SeasonService.Season currentSeason = seasonService.getCurrentSeason();

        // then
        assertThat(currentSeason).isNotNull();
    }

    @Test
    @DisplayName("현재 계절의 한글 이름을 반환해야 한다")
    void getCurrentSeasonKoreanName_ReturnsName() {
        // when
        String koreanName = seasonService.getCurrentSeasonKoreanName();

        // then
        assertThat(koreanName).isIn("봄", "여름", "가을", "겨울");
    }

    @Test
    @DisplayName("현재 계절의 영문 이름을 반환해야 한다")
    void getCurrentSeasonEnglishName_ReturnsName() {
        // when
        String englishName = seasonService.getCurrentSeasonEnglishName();

        // then
        assertThat(englishName).isIn("spring", "summer", "autumn", "winter");
    }
}
