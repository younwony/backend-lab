package dev.wony.backendlab.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Locale;

/**
 * 데이터 포맷 변환 유틸리티 클래스.
 * <p>
 * 다양한 데이터 포맷 변환 기능을 제공합니다:
 * - 날짜/시간 포맷팅 및 파싱
 * - 숫자 포맷팅 (통화, 퍼센트, 파일 크기)
 * - Base64 인코딩/디코딩
 * - 바이트 배열 변환
 */
public final class DataFormatUtils {

    // 날짜 포맷터
    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter DATE_SLASH = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter DATE_DOT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter DATE_KOREAN = DateTimeFormatter.ofPattern("yyyy'Y' MM'M' dd'D'");
    public static final DateTimeFormatter DATETIME_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATETIME_COMPACT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // 파일 크기 단위
    private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB", "TB", "PB"};
    private static final long KB = 1024L;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;

    private DataFormatUtils() {
        // 유틸리티 클래스 - 인스턴스화 불가
    }

    // ==================== 날짜/시간 포맷팅 ====================

    /**
     * LocalDate를 ISO 포맷 (yyyy-MM-dd)으로 변환
     *
     * @param date 포맷할 날짜
     * @return 포맷된 문자열, 입력이 null이면 null 반환
     */
    public static String formatDate(LocalDate date) {
        return date == null ? null : ISO_DATE.format(date);
    }

    /**
     * LocalDate를 지정된 패턴으로 포맷
     *
     * @param date    포맷할 날짜
     * @param pattern 패턴 (예: "yyyy/MM/dd")
     * @return 포맷된 문자열, 입력이 null이면 null 반환
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }

    /**
     * LocalDateTime을 전체 포맷 (yyyy-MM-dd HH:mm:ss)으로 변환
     *
     * @param dateTime 포맷할 날짜시간
     * @return 포맷된 문자열, 입력이 null이면 null 반환
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : DATETIME_FULL.format(dateTime);
    }

    /**
     * LocalDateTime을 지정된 패턴으로 포맷
     *
     * @param dateTime 포맷할 날짜시간
     * @param pattern  패턴
     * @return 포맷된 문자열, 입력이 null이면 null 반환
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(dateTime);
    }

    /**
     * 날짜 문자열을 LocalDate로 파싱 (ISO 포맷)
     *
     * @param dateStr 날짜 문자열 (yyyy-MM-dd)
     * @return 파싱된 LocalDate, 파싱 실패시 null 반환
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, ISO_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 날짜 문자열을 지정된 패턴으로 파싱
     *
     * @param dateStr 날짜 문자열
     * @param pattern 패턴
     * @return 파싱된 LocalDate, 파싱 실패시 null 반환
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isBlank() || pattern == null) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 날짜시간 문자열을 LocalDateTime으로 파싱
     *
     * @param dateTimeStr 날짜시간 문자열 (yyyy-MM-dd HH:mm:ss)
     * @return 파싱된 LocalDateTime, 파싱 실패시 null 반환
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FULL);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 에포크 밀리초를 LocalDateTime으로 변환
     *
     * @param epochMillis 에포크 밀리초
     * @return 시스템 기본 타임존의 LocalDateTime
     */
    public static LocalDateTime fromEpochMillis(long epochMillis) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(epochMillis),
                ZoneId.systemDefault()
        );
    }

    /**
     * LocalDateTime을 에포크 밀리초로 변환
     *
     * @param dateTime 날짜시간
     * @return 에포크 밀리초
     */
    public static long toEpochMillis(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0L;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 날짜를 상대적 시간으로 포맷 (예: "3일 전", "2시간 전")
     *
     * @param dateTime 포맷할 날짜시간
     * @return 상대적 시간 문자열
     */
    public static String toRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(dateTime, now).getSeconds();

        if (seconds < 0) {
            return "in the future";
        }
        if (seconds < 60) {
            return "just now";
        }
        if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        }
        if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        }
        if (seconds < 2592000) {
            long days = seconds / 86400;
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
        if (seconds < 31536000) {
            long months = seconds / 2592000;
            return months + " month" + (months > 1 ? "s" : "") + " ago";
        }

        long years = seconds / 31536000;
        return years + " year" + (years > 1 ? "s" : "") + " ago";
    }

    // ==================== 숫자 포맷팅 ====================

    /**
     * 숫자를 천 단위 구분자로 포맷
     *
     * @param number 포맷할 숫자
     * @return 포맷된 문자열 (예: "1,234,567")
     */
    public static String formatNumber(long number) {
        return String.format(Locale.US, "%,d", number);
    }

    /**
     * 소수를 지정된 소수점 자릿수로 포맷
     *
     * @param number        포맷할 숫자
     * @param decimalPlaces 소수점 자릿수
     * @return 포맷된 문자열
     */
    public static String formatDecimal(double number, int decimalPlaces) {
        if (decimalPlaces < 0) {
            decimalPlaces = 0;
        }
        StringBuilder pattern = new StringBuilder("#,##0");
        if (decimalPlaces > 0) {
            pattern.append(".");
            pattern.append("0".repeat(decimalPlaces));
        }
        return new DecimalFormat(pattern.toString()).format(number);
    }

    /**
     * 원화 통화 포맷으로 변환
     *
     * @param amount 금액
     * @return 포맷된 문자열 (예: "W1,234,567")
     */
    public static String formatKrw(long amount) {
        return "W" + formatNumber(amount);
    }

    /**
     * 달러 통화 포맷으로 변환
     *
     * @param amount 금액
     * @return 포맷된 문자열 (예: "$1,234.56")
     */
    public static String formatUsd(double amount) {
        return "$" + formatDecimal(amount, 2);
    }

    /**
     * 퍼센트 포맷으로 변환
     *
     * @param value         값 (0.0~1.0 또는 0~100)
     * @param decimalPlaces 소수점 자릿수
     * @param isRatio       true이면 비율(0-1), false이면 퍼센트(0-100)
     * @return 포맷된 문자열 (예: "75.5%")
     */
    public static String formatPercentage(double value, int decimalPlaces, boolean isRatio) {
        double percentage = isRatio ? value * 100 : value;
        return formatDecimal(percentage, decimalPlaces) + "%";
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형식으로 포맷
     *
     * @param bytes 바이트 크기
     * @return 포맷된 문자열 (예: "1.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 0) {
            return "0 B";
        }
        if (bytes < KB) {
            return bytes + " B";
        }

        int unitIndex = 0;
        double size = bytes;

        while (size >= KB && unitIndex < SIZE_UNITS.length - 1) {
            size /= KB;
            unitIndex++;
        }

        return formatDecimal(size, size < 10 ? 2 : 1) + " " + SIZE_UNITS[unitIndex];
    }

    /**
     * 파일 크기 문자열을 바이트로 파싱
     *
     * @param sizeStr 크기 문자열 (예: "1.5 MB", "500 KB")
     * @return 바이트 크기, 파싱 실패시 -1 반환
     */
    public static long parseFileSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isBlank()) {
            return -1;
        }

        sizeStr = sizeStr.trim().toUpperCase();
        String[] parts = sizeStr.split("\\s+");
        if (parts.length != 2) {
            return -1;
        }

        try {
            double value = Double.parseDouble(parts[0]);
            String unit = parts[1];

            return switch (unit) {
                case "B" -> (long) value;
                case "KB" -> (long) (value * KB);
                case "MB" -> (long) (value * MB);
                case "GB" -> (long) (value * GB);
                case "TB" -> (long) (value * TB);
                default -> -1;
            };
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 숫자를 지정된 소수점 자릿수로 반올림
     *
     * @param value         반올림할 값
     * @param decimalPlaces 소수점 자릿수
     * @return 반올림된 값
     */
    public static double round(double value, int decimalPlaces) {
        if (decimalPlaces < 0) {
            decimalPlaces = 0;
        }
        return BigDecimal.valueOf(value)
                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // ==================== Base64 인코딩/디코딩 ====================

    /**
     * 문자열을 Base64로 인코딩
     *
     * @param str 인코딩할 문자열
     * @return Base64 인코딩된 문자열
     */
    public static String toBase64(String str) {
        if (str == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    /**
     * Base64 문자열을 디코딩
     *
     * @param base64Str Base64 인코딩된 문자열
     * @return 디코딩된 문자열, 디코딩 실패시 null 반환
     */
    public static String fromBase64(String base64Str) {
        if (base64Str == null) {
            return null;
        }
        try {
            return new String(Base64.getDecoder().decode(base64Str));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 바이트 배열을 Base64로 인코딩
     *
     * @param bytes 바이트 배열
     * @return Base64 인코딩된 문자열
     */
    public static String toBase64(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64를 바이트 배열로 디코딩
     *
     * @param base64Str Base64 인코딩된 문자열
     * @return 디코딩된 바이트 배열, 디코딩 실패시 null 반환
     */
    public static byte[] fromBase64ToBytes(String base64Str) {
        if (base64Str == null) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(base64Str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ==================== Hex 인코딩/디코딩 ====================

    /**
     * 바이트 배열을 16진수 문자열로 변환
     *
     * @param bytes 바이트 배열
     * @return 16진수 문자열
     */
    public static String toHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    /**
     * 16진수 문자열을 바이트 배열로 변환
     *
     * @param hexStr 16진수 문자열
     * @return 바이트 배열, 유효하지 않으면 null 반환
     */
    public static byte[] fromHex(String hexStr) {
        if (hexStr == null || hexStr.length() % 2 != 0) {
            return null;
        }
        try {
            int len = hexStr.length();
            byte[] bytes = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                bytes[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                        + Character.digit(hexStr.charAt(i + 1), 16));
            }
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== 전화번호/카드번호 포맷팅 ====================

    /**
     * 전화번호를 하이픈으로 포맷 (한국 형식)
     *
     * @param phoneNumber 전화번호 (숫자만)
     * @return 포맷된 전화번호 (예: "010-1234-5678")
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        String digits = phoneNumber.replaceAll("[^0-9]", "");

        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        } else if (digits.length() == 10) {
            if (digits.startsWith("02")) {
                return digits.substring(0, 2) + "-" + digits.substring(2, 6) + "-" + digits.substring(6);
            }
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        } else if (digits.length() == 9) {
            return digits.substring(0, 2) + "-" + digits.substring(2, 5) + "-" + digits.substring(5);
        }
        return phoneNumber;
    }

    /**
     * 신용카드 번호를 공백으로 포맷
     *
     * @param cardNumber 카드번호 (숫자만)
     * @return 포맷된 카드번호 (예: "1234 5678 9012 3456")
     */
    public static String formatCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        String digits = cardNumber.replaceAll("[^0-9]", "");

        if (digits.length() != 16) {
            return cardNumber;
        }

        return digits.substring(0, 4) + " " +
                digits.substring(4, 8) + " " +
                digits.substring(8, 12) + " " +
                digits.substring(12);
    }

    /**
     * 신용카드 번호 마스킹 (마지막 4자리만 표시)
     *
     * @param cardNumber 카드번호
     * @return 마스킹된 카드번호 (예: "**** **** **** 3456")
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        String digits = cardNumber.replaceAll("[^0-9]", "");

        if (digits.length() < 4) {
            return "*".repeat(digits.length());
        }

        String lastFour = digits.substring(digits.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
