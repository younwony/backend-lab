package dev.wony.backendlab.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 문자열 처리 유틸리티 클래스
 * <p>
 * 다양한 문자열 변환, 검증, 조작 메서드를 제공합니다.
 * 모든 메서드는 null-safe하게 설계되었습니다.
 */
public final class StringUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\d{2,3}-\\d{3,4}-\\d{4}$"
    );

    private StringUtils() {
        // 유틸리티 클래스는 인스턴스화하지 않음
    }

    /**
     * 문자열이 null이거나 빈 문자열인지 확인
     *
     * @param str 검사할 문자열
     * @return null이거나 빈 문자열이면 true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 문자열이 null이거나 공백만 포함하는지 확인
     *
     * @param str 검사할 문자열
     * @return null이거나 공백만 있으면 true
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 문자열이 비어있지 않은지 확인
     *
     * @param str 검사할 문자열
     * @return 비어있지 않으면 true
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 문자열이 공백이 아닌지 확인
     *
     * @param str 검사할 문자열
     * @return 공백이 아니면 true
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 문자열의 첫 글자를 대문자로 변환
     *
     * @param str 변환할 문자열
     * @return 첫 글자가 대문자로 변환된 문자열, null이면 null 반환
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 문자열의 첫 글자를 소문자로 변환
     *
     * @param str 변환할 문자열
     * @return 첫 글자가 소문자로 변환된 문자열, null이면 null 반환
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 문자열을 뒤집음
     *
     * @param str 뒤집을 문자열
     * @return 뒤집힌 문자열, null이면 null 반환
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 문자열이 회문(팰린드롬)인지 확인
     *
     * @param str 검사할 문자열
     * @return 회문이면 true
     */
    public static boolean isPalindrome(String str) {
        if (isEmpty(str)) {
            return true;
        }
        String cleaned = str.toLowerCase().replaceAll("[^a-z0-9]", "");
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    /**
     * 문자열에서 특정 문자의 개수를 셈
     *
     * @param str 검사할 문자열
     * @param ch  찾을 문자
     * @return 문자의 개수
     */
    public static int countChar(String str, char ch) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }

    /**
     * 문자열에서 모음의 개수를 셈 (영문 기준)
     *
     * @param str 검사할 문자열
     * @return 모음의 개수
     */
    public static int countVowels(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        String vowels = "aeiouAEIOU";
        for (char c : str.toCharArray()) {
            if (vowels.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }

    /**
     * 문자열의 단어 개수를 셈
     *
     * @param str 검사할 문자열
     * @return 단어의 개수
     */
    public static int countWords(String str) {
        if (isBlank(str)) {
            return 0;
        }
        return str.trim().split("\\s+").length;
    }

    /**
     * 문자열을 지정된 길이로 자르고 말줄임표 추가
     *
     * @param str       원본 문자열
     * @param maxLength 최대 길이 (말줄임표 포함)
     * @return 잘린 문자열
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || maxLength < 0) {
            return str;
        }
        if (maxLength <= 3) {
            return str.length() <= maxLength ? str : str.substring(0, maxLength);
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * 이메일 형식이 유효한지 검증
     *
     * @param email 검사할 이메일
     * @return 유효하면 true
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 전화번호 형식이 유효한지 검증 (한국 형식: 010-1234-5678)
     *
     * @param phone 검사할 전화번호
     * @return 유효하면 true
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * camelCase를 snake_case로 변환
     *
     * @param str camelCase 문자열
     * @return snake_case 문자열
     */
    public static String camelToSnake(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * snake_case를 camelCase로 변환
     *
     * @param str snake_case 문자열
     * @return camelCase 문자열
     */
    public static String snakeToCamel(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        for (char ch : str.toCharArray()) {
            if (ch == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(ch));
                capitalizeNext = false;
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 문자열을 마스킹 처리 (중간 부분을 *로 대체)
     *
     * @param str        마스킹할 문자열
     * @param visibleLen 앞뒤로 보이는 문자 수
     * @return 마스킹된 문자열
     */
    public static String mask(String str, int visibleLen) {
        if (isEmpty(str) || visibleLen < 0) {
            return str;
        }
        int len = str.length();
        if (len <= visibleLen * 2) {
            return "*".repeat(len);
        }
        String prefix = str.substring(0, visibleLen);
        String suffix = str.substring(len - visibleLen);
        String masked = "*".repeat(len - visibleLen * 2);
        return prefix + masked + suffix;
    }

    /**
     * 문자열에서 중복 문자를 제거
     *
     * @param str 원본 문자열
     * @return 중복이 제거된 문자열
     */
    public static String removeDuplicateChars(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (char ch : str.toCharArray()) {
            if (result.indexOf(String.valueOf(ch)) == -1) {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 문자열에서 가장 많이 나타나는 문자를 반환
     *
     * @param str 검사할 문자열
     * @return 가장 많이 나타나는 문자, 빈 문자열이면 null
     */
    public static Character mostFrequentChar(String str) {
        if (isEmpty(str)) {
            return null;
        }
        int[] counts = new int[256];
        for (char ch : str.toCharArray()) {
            counts[ch]++;
        }
        char result = str.charAt(0);
        int maxCount = 0;
        for (int i = 0; i < 256; i++) {
            if (counts[i] > maxCount) {
                maxCount = counts[i];
                result = (char) i;
            }
        }
        return result;
    }

    /**
     * 두 문자열이 애너그램인지 확인
     *
     * @param str1 첫 번째 문자열
     * @param str2 두 번째 문자열
     * @return 애너그램이면 true
     */
    public static boolean isAnagram(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        }
        String s1 = str1.toLowerCase().replaceAll("\\s", "");
        String s2 = str2.toLowerCase().replaceAll("\\s", "");
        if (s1.length() != s2.length()) {
            return false;
        }
        List<Character> chars1 = new ArrayList<>();
        List<Character> chars2 = new ArrayList<>();
        for (char c : s1.toCharArray()) chars1.add(c);
        for (char c : s2.toCharArray()) chars2.add(c);
        Collections.sort(chars1);
        Collections.sort(chars2);
        return chars1.equals(chars2);
    }
}
