package dev.wony.backendlab.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataFormatUtils 테스트")
class DataFormatUtilsTest {

    @Nested
    @DisplayName("날짜/시간 포맷팅 테스트")
    class DateTimeFormattingTest {

        @Test
        @DisplayName("formatDate - null이면 null 반환")
        void formatDate_null_returnsNull() {
            assertNull(DataFormatUtils.formatDate(null));
        }

        @Test
        @DisplayName("formatDate - ISO 포맷으로 변환")
        void formatDate_validDate_formatsCorrectly() {
            LocalDate date = LocalDate.of(2024, 3, 15);
            assertEquals("2024-03-15", DataFormatUtils.formatDate(date));
        }

        @ParameterizedTest
        @CsvSource({
                "yyyy/MM/dd, 2024/03/15",
                "yyyy.MM.dd, 2024.03.15",
                "yyyyMMdd, 20240315"
        })
        @DisplayName("formatDate - 커스텀 패턴으로 포맷")
        void formatDate_customPattern_formatsCorrectly(String pattern, String expected) {
            LocalDate date = LocalDate.of(2024, 3, 15);
            assertEquals(expected, DataFormatUtils.formatDate(date, pattern));
        }

        @Test
        @DisplayName("formatDateTime - null이면 null 반환")
        void formatDateTime_null_returnsNull() {
            assertNull(DataFormatUtils.formatDateTime(null));
        }

        @Test
        @DisplayName("formatDateTime - 전체 포맷으로 변환")
        void formatDateTime_validDateTime_formatsCorrectly() {
            LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 14, 30, 45);
            assertEquals("2024-03-15 14:30:45", DataFormatUtils.formatDateTime(dateTime));
        }

        @Test
        @DisplayName("formatDateTime - 커스텀 패턴으로 포맷")
        void formatDateTime_customPattern_formatsCorrectly() {
            LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 14, 30, 45);
            assertEquals("20240315143045", DataFormatUtils.formatDateTime(dateTime, "yyyyMMddHHmmss"));
        }
    }

    @Nested
    @DisplayName("날짜 파싱 테스트")
    class DateParsingTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("parseDate - null/빈문자열/공백이면 null 반환")
        void parseDate_nullOrEmpty_returnsNull(String input) {
            assertNull(DataFormatUtils.parseDate(input));
        }

        @Test
        @DisplayName("parseDate - ISO 포맷 파싱")
        void parseDate_validIsoDate_parsesCorrectly() {
            LocalDate result = DataFormatUtils.parseDate("2024-03-15");
            assertEquals(LocalDate.of(2024, 3, 15), result);
        }

        @Test
        @DisplayName("parseDate - 잘못된 포맷이면 null 반환")
        void parseDate_invalidFormat_returnsNull() {
            assertNull(DataFormatUtils.parseDate("invalid-date"));
        }

        @Test
        @DisplayName("parseDate - 커스텀 패턴으로 파싱")
        void parseDate_customPattern_parsesCorrectly() {
            LocalDate result = DataFormatUtils.parseDate("2024/03/15", "yyyy/MM/dd");
            assertEquals(LocalDate.of(2024, 3, 15), result);
        }

        @Test
        @DisplayName("parseDateTime - 전체 포맷 파싱")
        void parseDateTime_validDateTime_parsesCorrectly() {
            LocalDateTime result = DataFormatUtils.parseDateTime("2024-03-15 14:30:45");
            assertEquals(LocalDateTime.of(2024, 3, 15, 14, 30, 45), result);
        }

        @Test
        @DisplayName("parseDateTime - 잘못된 포맷이면 null 반환")
        void parseDateTime_invalidFormat_returnsNull() {
            assertNull(DataFormatUtils.parseDateTime("invalid"));
        }
    }

    @Nested
    @DisplayName("에포크 변환 테스트")
    class EpochConversionTest {

        @Test
        @DisplayName("fromEpochMillis - 정상 변환")
        void fromEpochMillis_validMillis_convertsCorrectly() {
            LocalDateTime result = DataFormatUtils.fromEpochMillis(0);
            assertNotNull(result);
        }

        @Test
        @DisplayName("toEpochMillis - null이면 0 반환")
        void toEpochMillis_null_returnsZero() {
            assertEquals(0L, DataFormatUtils.toEpochMillis(null));
        }

        @Test
        @DisplayName("에포크 변환 - 왕복 테스트")
        void epochConversion_roundTrip_works() {
            LocalDateTime original = LocalDateTime.of(2024, 3, 15, 14, 30, 0);
            long millis = DataFormatUtils.toEpochMillis(original);
            LocalDateTime result = DataFormatUtils.fromEpochMillis(millis);
            assertEquals(original, result);
        }
    }

    @Nested
    @DisplayName("상대적 시간 테스트")
    class RelativeTimeTest {

        @Test
        @DisplayName("toRelativeTime - null이면 null 반환")
        void toRelativeTime_null_returnsNull() {
            assertNull(DataFormatUtils.toRelativeTime(null));
        }

        @Test
        @DisplayName("toRelativeTime - 방금 전")
        void toRelativeTime_withinMinute_returnsJustNow() {
            LocalDateTime recent = LocalDateTime.now().minusSeconds(30);
            assertEquals("just now", DataFormatUtils.toRelativeTime(recent));
        }

        @Test
        @DisplayName("toRelativeTime - 분 전")
        void toRelativeTime_minutesAgo_returnsMinutes() {
            LocalDateTime past = LocalDateTime.now().minusMinutes(5);
            assertEquals("5 minutes ago", DataFormatUtils.toRelativeTime(past));
        }

        @Test
        @DisplayName("toRelativeTime - 시간 전")
        void toRelativeTime_hoursAgo_returnsHours() {
            LocalDateTime past = LocalDateTime.now().minusHours(3);
            assertEquals("3 hours ago", DataFormatUtils.toRelativeTime(past));
        }

        @Test
        @DisplayName("toRelativeTime - 일 전")
        void toRelativeTime_daysAgo_returnsDays() {
            LocalDateTime past = LocalDateTime.now().minusDays(5);
            assertEquals("5 days ago", DataFormatUtils.toRelativeTime(past));
        }

        @Test
        @DisplayName("toRelativeTime - 미래 시간")
        void toRelativeTime_future_returnsFuture() {
            LocalDateTime future = LocalDateTime.now().plusDays(1);
            assertEquals("in the future", DataFormatUtils.toRelativeTime(future));
        }
    }

    @Nested
    @DisplayName("숫자 포맷팅 테스트")
    class NumberFormattingTest {

        @ParameterizedTest
        @CsvSource({
                "1000, '1,000'",
                "1234567, '1,234,567'",
                "100, '100'",
                "0, '0'"
        })
        @DisplayName("formatNumber - 천 단위 구분자 포맷")
        void formatNumber_validNumber_formatsCorrectly(long input, String expected) {
            assertEquals(expected, DataFormatUtils.formatNumber(input));
        }

        @ParameterizedTest
        @CsvSource({
                "1234.567, 2, '1,234.57'",
                "1234.6, 0, '1,235'",
                "0.123, 3, '0.123'"
        })
        @DisplayName("formatDecimal - 소수점 자릿수 포맷")
        void formatDecimal_validNumber_formatsCorrectly(double input, int places, String expected) {
            assertEquals(expected, DataFormatUtils.formatDecimal(input, places));
        }

        @Test
        @DisplayName("formatKrw - 원화 포맷")
        void formatKrw_validAmount_formatsCorrectly() {
            assertEquals("W1,234,567", DataFormatUtils.formatKrw(1234567));
        }

        @Test
        @DisplayName("formatUsd - 달러 포맷")
        void formatUsd_validAmount_formatsCorrectly() {
            assertEquals("$1,234.56", DataFormatUtils.formatUsd(1234.56));
        }

        @ParameterizedTest
        @CsvSource({
                "0.755, 1, true, '75.5%'",
                "75.5, 1, false, '75.5%'",
                "0.5, 0, true, '50%'"
        })
        @DisplayName("formatPercentage - 퍼센트 포맷")
        void formatPercentage_validValue_formatsCorrectly(double value, int places, boolean isRatio, String expected) {
            assertEquals(expected, DataFormatUtils.formatPercentage(value, places, isRatio));
        }
    }

    @Nested
    @DisplayName("파일 크기 포맷팅 테스트")
    class FileSizeFormattingTest {

        @ParameterizedTest
        @CsvSource({
                "0, '0 B'",
                "500, '500 B'",
                "1024, '1.00 KB'",
                "1536, '1.50 KB'",
                "1048576, '1.00 MB'",
                "1073741824, '1.00 GB'",
                "1099511627776, '1.00 TB'"
        })
        @DisplayName("formatFileSize - 정상 포맷")
        void formatFileSize_validSize_formatsCorrectly(long bytes, String expected) {
            assertEquals(expected, DataFormatUtils.formatFileSize(bytes));
        }

        @Test
        @DisplayName("formatFileSize - 음수면 0 B 반환")
        void formatFileSize_negative_returnsZero() {
            assertEquals("0 B", DataFormatUtils.formatFileSize(-100));
        }

        @ParameterizedTest
        @CsvSource({
                "'1 KB', 1024",
                "'1.5 MB', 1572864",
                "'500 B', 500",
                "'2 GB', 2147483648"
        })
        @DisplayName("parseFileSize - 정상 파싱")
        void parseFileSize_validString_parsesCorrectly(String input, long expected) {
            assertEquals(expected, DataFormatUtils.parseFileSize(input));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"invalid", "1234", "1 XX"})
        @DisplayName("parseFileSize - 유효하지 않으면 -1 반환")
        void parseFileSize_invalid_returnsNegativeOne(String input) {
            assertEquals(-1, DataFormatUtils.parseFileSize(input));
        }
    }

    @Nested
    @DisplayName("반올림 테스트")
    class RoundTest {

        @ParameterizedTest
        @CsvSource({
                "1.234, 2, 1.23",
                "1.235, 2, 1.24",
                "1.5, 0, 2.0",
                "1.4, 0, 1.0"
        })
        @DisplayName("round - 정상 반올림")
        void round_validValue_roundsCorrectly(double input, int places, double expected) {
            assertEquals(expected, DataFormatUtils.round(input, places));
        }

        @Test
        @DisplayName("round - 음수 자릿수는 0으로 처리")
        void round_negativeDecimalPlaces_treatsAsZero() {
            assertEquals(123.0, DataFormatUtils.round(123.456, -1));
        }
    }

    @Nested
    @DisplayName("Base64 인코딩/디코딩 테스트")
    class Base64Test {

        @Test
        @DisplayName("toBase64 - null이면 null 반환")
        void toBase64_null_returnsNull() {
            assertNull(DataFormatUtils.toBase64((String) null));
        }

        @Test
        @DisplayName("toBase64/fromBase64 - 왕복 테스트")
        void base64_roundTrip_works() {
            String original = "Hello, World!";
            String encoded = DataFormatUtils.toBase64(original);
            String decoded = DataFormatUtils.fromBase64(encoded);
            assertEquals(original, decoded);
        }

        @Test
        @DisplayName("fromBase64 - 유효하지 않으면 null 반환")
        void fromBase64_invalid_returnsNull() {
            assertNull(DataFormatUtils.fromBase64("not-valid-base64!!!"));
        }

        @Test
        @DisplayName("toBase64 바이트 배열 - 정상 동작")
        void toBase64Bytes_validBytes_encodesCorrectly() {
            byte[] bytes = {0x01, 0x02, 0x03};
            String encoded = DataFormatUtils.toBase64(bytes);
            assertNotNull(encoded);

            byte[] decoded = DataFormatUtils.fromBase64ToBytes(encoded);
            assertArrayEquals(bytes, decoded);
        }
    }

    @Nested
    @DisplayName("Hex 인코딩/디코딩 테스트")
    class HexTest {

        @Test
        @DisplayName("toHex - null이면 null 반환")
        void toHex_null_returnsNull() {
            assertNull(DataFormatUtils.toHex(null));
        }

        @Test
        @DisplayName("toHex - 정상 인코딩")
        void toHex_validBytes_encodesCorrectly() {
            byte[] bytes = {0x01, 0x0A, (byte) 0xFF};
            assertEquals("010aff", DataFormatUtils.toHex(bytes));
        }

        @Test
        @DisplayName("fromHex - 정상 디코딩")
        void fromHex_validHex_decodesCorrectly() {
            byte[] result = DataFormatUtils.fromHex("010aff");
            assertArrayEquals(new byte[]{0x01, 0x0A, (byte) 0xFF}, result);
        }

        @Test
        @DisplayName("fromHex - null이면 null 반환")
        void fromHex_null_returnsNull() {
            assertNull(DataFormatUtils.fromHex(null));
        }

        @Test
        @DisplayName("fromHex - 홀수 길이면 null 반환")
        void fromHex_oddLength_returnsNull() {
            assertNull(DataFormatUtils.fromHex("abc"));
        }

        @Test
        @DisplayName("Hex 왕복 테스트")
        void hex_roundTrip_works() {
            byte[] original = {0x00, 0x7F, (byte) 0x80, (byte) 0xFF};
            String hex = DataFormatUtils.toHex(original);
            byte[] decoded = DataFormatUtils.fromHex(hex);
            assertArrayEquals(original, decoded);
        }
    }

    @Nested
    @DisplayName("전화번호 포맷팅 테스트")
    class PhoneNumberFormattingTest {

        @Test
        @DisplayName("formatPhoneNumber - null이면 null 반환")
        void formatPhoneNumber_null_returnsNull() {
            assertNull(DataFormatUtils.formatPhoneNumber(null));
        }

        @ParameterizedTest
        @CsvSource({
                "01012345678, 010-1234-5678",
                "0212345678, 02-1234-5678",
                "0311234567, 031-123-4567",
                "021234567, 02-123-4567"
        })
        @DisplayName("formatPhoneNumber - 한국 전화번호 포맷")
        void formatPhoneNumber_validNumber_formatsCorrectly(String input, String expected) {
            assertEquals(expected, DataFormatUtils.formatPhoneNumber(input));
        }

        @Test
        @DisplayName("formatPhoneNumber - 이미 포맷된 입력 처리")
        void formatPhoneNumber_alreadyFormatted_formatsCorrectly() {
            assertEquals("010-1234-5678", DataFormatUtils.formatPhoneNumber("010-1234-5678"));
        }
    }

    @Nested
    @DisplayName("카드번호 포맷팅 테스트")
    class CardNumberFormattingTest {

        @Test
        @DisplayName("formatCardNumber - null이면 null 반환")
        void formatCardNumber_null_returnsNull() {
            assertNull(DataFormatUtils.formatCardNumber(null));
        }

        @Test
        @DisplayName("formatCardNumber - 16자리 카드 포맷")
        void formatCardNumber_valid16Digits_formatsCorrectly() {
            assertEquals("1234 5678 9012 3456", DataFormatUtils.formatCardNumber("1234567890123456"));
        }

        @Test
        @DisplayName("formatCardNumber - 잘못된 길이면 원본 반환")
        void formatCardNumber_invalidLength_returnsOriginal() {
            assertEquals("12345", DataFormatUtils.formatCardNumber("12345"));
        }

        @Test
        @DisplayName("maskCardNumber - null이면 null 반환")
        void maskCardNumber_null_returnsNull() {
            assertNull(DataFormatUtils.maskCardNumber(null));
        }

        @Test
        @DisplayName("maskCardNumber - 정상 마스킹")
        void maskCardNumber_validCard_masksCorrectly() {
            assertEquals("**** **** **** 3456", DataFormatUtils.maskCardNumber("1234567890123456"));
        }

        @Test
        @DisplayName("maskCardNumber - 짧은 번호는 전체 마스킹")
        void maskCardNumber_shortNumber_masksAll() {
            assertEquals("***", DataFormatUtils.maskCardNumber("123"));
        }
    }
}
