package dev.wony.backendlab.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils 테스트")
class StringUtilsTest {

    @Nested
    @DisplayName("isEmpty / isBlank 테스트")
    class EmptyBlankTest {

        @Test
        @DisplayName("isEmpty - null이면 true 반환")
        void isEmpty_null_returnsTrue() {
            assertTrue(StringUtils.isEmpty(null));
        }

        @Test
        @DisplayName("isEmpty - 빈 문자열이면 true 반환")
        void isEmpty_emptyString_returnsTrue() {
            assertTrue(StringUtils.isEmpty(""));
        }

        @Test
        @DisplayName("isEmpty - 공백만 있으면 false 반환")
        void isEmpty_whitespace_returnsFalse() {
            assertFalse(StringUtils.isEmpty("   "));
        }

        @Test
        @DisplayName("isEmpty - 내용이 있으면 false 반환")
        void isEmpty_withContent_returnsFalse() {
            assertFalse(StringUtils.isEmpty("hello"));
        }

        @Test
        @DisplayName("isBlank - null이면 true 반환")
        void isBlank_null_returnsTrue() {
            assertTrue(StringUtils.isBlank(null));
        }

        @Test
        @DisplayName("isBlank - 공백만 있으면 true 반환")
        void isBlank_whitespace_returnsTrue() {
            assertTrue(StringUtils.isBlank("   "));
        }

        @Test
        @DisplayName("isBlank - 내용이 있으면 false 반환")
        void isBlank_withContent_returnsFalse() {
            assertFalse(StringUtils.isBlank("hello"));
        }

        @Test
        @DisplayName("isNotEmpty - 내용이 있으면 true 반환")
        void isNotEmpty_withContent_returnsTrue() {
            assertTrue(StringUtils.isNotEmpty("hello"));
        }

        @Test
        @DisplayName("isNotBlank - 내용이 있으면 true 반환")
        void isNotBlank_withContent_returnsTrue() {
            assertTrue(StringUtils.isNotBlank("hello"));
        }
    }

    @Nested
    @DisplayName("capitalize / uncapitalize 테스트")
    class CapitalizeTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("capitalize - null 또는 빈 문자열은 그대로 반환")
        void capitalize_nullOrEmpty_returnsSame(String input) {
            assertEquals(input, StringUtils.capitalize(input));
        }

        @ParameterizedTest
        @CsvSource({
                "hello, Hello",
                "Hello, Hello",
                "h, H",
                "123abc, 123abc"
        })
        @DisplayName("capitalize - 첫 글자를 대문자로 변환")
        void capitalize_validString_capitalizesFirstChar(String input, String expected) {
            assertEquals(expected, StringUtils.capitalize(input));
        }

        @ParameterizedTest
        @CsvSource({
                "Hello, hello",
                "hello, hello",
                "H, h",
                "123ABC, 123ABC"
        })
        @DisplayName("uncapitalize - 첫 글자를 소문자로 변환")
        void uncapitalize_validString_uncapitalizesFirstChar(String input, String expected) {
            assertEquals(expected, StringUtils.uncapitalize(input));
        }
    }

    @Nested
    @DisplayName("reverse 테스트")
    class ReverseTest {

        @Test
        @DisplayName("reverse - null이면 null 반환")
        void reverse_null_returnsNull() {
            assertNull(StringUtils.reverse(null));
        }

        @Test
        @DisplayName("reverse - 빈 문자열이면 빈 문자열 반환")
        void reverse_empty_returnsEmpty() {
            assertEquals("", StringUtils.reverse(""));
        }

        @ParameterizedTest
        @CsvSource({
                "hello, olleh",
                "a, a",
                "ab, ba",
                "12345, 54321"
        })
        @DisplayName("reverse - 문자열을 뒤집음")
        void reverse_validString_reversesIt(String input, String expected) {
            assertEquals(expected, StringUtils.reverse(input));
        }
    }

    @Nested
    @DisplayName("isPalindrome 테스트")
    class PalindromeTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("isPalindrome - null 또는 빈 문자열은 true 반환")
        void isPalindrome_nullOrEmpty_returnsTrue(String input) {
            assertTrue(StringUtils.isPalindrome(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "aa", "aba", "abba", "A man a plan a canal Panama", "Was it a car or a cat I saw"})
        @DisplayName("isPalindrome - 회문이면 true 반환")
        void isPalindrome_palindrome_returnsTrue(String input) {
            assertTrue(StringUtils.isPalindrome(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {"ab", "hello", "world"})
        @DisplayName("isPalindrome - 회문이 아니면 false 반환")
        void isPalindrome_notPalindrome_returnsFalse(String input) {
            assertFalse(StringUtils.isPalindrome(input));
        }
    }

    @Nested
    @DisplayName("countChar / countVowels / countWords 테스트")
    class CountTest {

        @Test
        @DisplayName("countChar - null이면 0 반환")
        void countChar_null_returnsZero() {
            assertEquals(0, StringUtils.countChar(null, 'a'));
        }

        @ParameterizedTest
        @CsvSource({
                "hello, l, 2",
                "hello, o, 1",
                "hello, x, 0",
                "aaa, a, 3"
        })
        @DisplayName("countChar - 특정 문자 개수를 셈")
        void countChar_validString_countsCorrectly(String input, char ch, int expected) {
            assertEquals(expected, StringUtils.countChar(input, ch));
        }

        @Test
        @DisplayName("countVowels - null이면 0 반환")
        void countVowels_null_returnsZero() {
            assertEquals(0, StringUtils.countVowels(null));
        }

        @ParameterizedTest
        @CsvSource({
                "hello, 2",
                "aeiou, 5",
                "AEIOU, 5",
                "xyz, 0",
                "programming, 3"
        })
        @DisplayName("countVowels - 모음 개수를 셈")
        void countVowels_validString_countsCorrectly(String input, int expected) {
            assertEquals(expected, StringUtils.countVowels(input));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("countWords - null 또는 빈 문자열은 0 반환")
        void countWords_nullOrEmpty_returnsZero(String input) {
            assertEquals(0, StringUtils.countWords(input));
        }

        @Test
        @DisplayName("countWords - 공백만 있으면 0 반환")
        void countWords_whitespaceOnly_returnsZero() {
            assertEquals(0, StringUtils.countWords("   "));
        }

        @ParameterizedTest
        @CsvSource({
                "hello, 1",
                "hello world, 2",
                "one two three, 3",
                "'hello   world', 2"
        })
        @DisplayName("countWords - 단어 개수를 셈")
        void countWords_validString_countsCorrectly(String input, int expected) {
            assertEquals(expected, StringUtils.countWords(input));
        }
    }

    @Nested
    @DisplayName("truncate 테스트")
    class TruncateTest {

        @Test
        @DisplayName("truncate - null이면 null 반환")
        void truncate_null_returnsNull() {
            assertNull(StringUtils.truncate(null, 10));
        }

        @Test
        @DisplayName("truncate - 최대 길이보다 짧으면 그대로 반환")
        void truncate_shorterThanMax_returnsSame() {
            assertEquals("hello", StringUtils.truncate("hello", 10));
        }

        @Test
        @DisplayName("truncate - 최대 길이보다 길면 말줄임표 추가")
        void truncate_longerThanMax_truncatesWithEllipsis() {
            assertEquals("hello w...", StringUtils.truncate("hello world", 10));
        }

        @Test
        @DisplayName("truncate - 최대 길이가 3 이하면 말줄임표 없이 자름")
        void truncate_maxLengthThreeOrLess_truncatesWithoutEllipsis() {
            assertEquals("hel", StringUtils.truncate("hello", 3));
        }
    }

    @Nested
    @DisplayName("isValidEmail 테스트")
    class EmailValidationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("isValidEmail - null 또는 빈 문자열은 false 반환")
        void isValidEmail_nullOrEmpty_returnsFalse(String input) {
            assertFalse(StringUtils.isValidEmail(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "test@example.com",
                "user.name@domain.co.kr",
                "user+tag@example.org"
        })
        @DisplayName("isValidEmail - 유효한 이메일은 true 반환")
        void isValidEmail_validEmail_returnsTrue(String email) {
            assertTrue(StringUtils.isValidEmail(email));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "invalid",
                "invalid@",
                "@example.com",
                "invalid@.com",
                "invalid@com"
        })
        @DisplayName("isValidEmail - 유효하지 않은 이메일은 false 반환")
        void isValidEmail_invalidEmail_returnsFalse(String email) {
            assertFalse(StringUtils.isValidEmail(email));
        }
    }

    @Nested
    @DisplayName("isValidPhoneNumber 테스트")
    class PhoneValidationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("isValidPhoneNumber - null 또는 빈 문자열은 false 반환")
        void isValidPhoneNumber_nullOrEmpty_returnsFalse(String input) {
            assertFalse(StringUtils.isValidPhoneNumber(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "010-1234-5678",
                "02-123-4567",
                "031-1234-5678"
        })
        @DisplayName("isValidPhoneNumber - 유효한 전화번호는 true 반환")
        void isValidPhoneNumber_validPhone_returnsTrue(String phone) {
            assertTrue(StringUtils.isValidPhoneNumber(phone));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "01012345678",
                "010-12345678",
                "010-123-567",
                "invalid"
        })
        @DisplayName("isValidPhoneNumber - 유효하지 않은 전화번호는 false 반환")
        void isValidPhoneNumber_invalidPhone_returnsFalse(String phone) {
            assertFalse(StringUtils.isValidPhoneNumber(phone));
        }
    }

    @Nested
    @DisplayName("camelToSnake / snakeToCamel 테스트")
    class CaseConversionTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("camelToSnake - null 또는 빈 문자열은 그대로 반환")
        void camelToSnake_nullOrEmpty_returnsSame(String input) {
            assertEquals(input, StringUtils.camelToSnake(input));
        }

        @ParameterizedTest
        @CsvSource({
                "camelCase, camel_case",
                "myVariableName, my_variable_name",
                "simple, simple",
                "ABC, a_b_c"
        })
        @DisplayName("camelToSnake - camelCase를 snake_case로 변환")
        void camelToSnake_validString_convertsCorrectly(String input, String expected) {
            assertEquals(expected, StringUtils.camelToSnake(input));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("snakeToCamel - null 또는 빈 문자열은 그대로 반환")
        void snakeToCamel_nullOrEmpty_returnsSame(String input) {
            assertEquals(input, StringUtils.snakeToCamel(input));
        }

        @ParameterizedTest
        @CsvSource({
                "snake_case, snakeCase",
                "my_variable_name, myVariableName",
                "simple, simple"
        })
        @DisplayName("snakeToCamel - snake_case를 camelCase로 변환")
        void snakeToCamel_validString_convertsCorrectly(String input, String expected) {
            assertEquals(expected, StringUtils.snakeToCamel(input));
        }
    }

    @Nested
    @DisplayName("mask 테스트")
    class MaskTest {

        @Test
        @DisplayName("mask - null이면 null 반환")
        void mask_null_returnsNull() {
            assertNull(StringUtils.mask(null, 2));
        }

        @Test
        @DisplayName("mask - 짧은 문자열은 전체 마스킹")
        void mask_shortString_masksAll() {
            assertEquals("****", StringUtils.mask("1234", 2));
        }

        @ParameterizedTest
        @CsvSource({
                "1234567890, 2, 12******90",
                "hello@example.com, 3, hel***********com",
                "abcdef, 1, a****f"
        })
        @DisplayName("mask - 문자열 중간을 마스킹")
        void mask_validString_masksMiddle(String input, int visibleLen, String expected) {
            assertEquals(expected, StringUtils.mask(input, visibleLen));
        }
    }

    @Nested
    @DisplayName("removeDuplicateChars 테스트")
    class RemoveDuplicatesTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("removeDuplicateChars - null 또는 빈 문자열은 그대로 반환")
        void removeDuplicateChars_nullOrEmpty_returnsSame(String input) {
            assertEquals(input, StringUtils.removeDuplicateChars(input));
        }

        @ParameterizedTest
        @CsvSource({
                "hello, helo",
                "aabbcc, abc",
                "abcdef, abcdef",
                "aaa, a"
        })
        @DisplayName("removeDuplicateChars - 중복 문자를 제거")
        void removeDuplicateChars_validString_removesDuplicates(String input, String expected) {
            assertEquals(expected, StringUtils.removeDuplicateChars(input));
        }
    }

    @Nested
    @DisplayName("mostFrequentChar 테스트")
    class MostFrequentCharTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("mostFrequentChar - null 또는 빈 문자열은 null 반환")
        void mostFrequentChar_nullOrEmpty_returnsNull(String input) {
            assertNull(StringUtils.mostFrequentChar(input));
        }

        @Test
        @DisplayName("mostFrequentChar - 가장 많이 나타나는 문자 반환")
        void mostFrequentChar_validString_returnsMostFrequent() {
            assertEquals('l', StringUtils.mostFrequentChar("hello"));
        }

        @Test
        @DisplayName("mostFrequentChar - 모든 문자가 같은 빈도면 첫 번째 문자 반환")
        void mostFrequentChar_allSameFrequency_returnsFirst() {
            assertEquals('a', StringUtils.mostFrequentChar("abc"));
        }
    }

    @Nested
    @DisplayName("isAnagram 테스트")
    class AnagramTest {

        @Test
        @DisplayName("isAnagram - 둘 다 null이면 true 반환")
        void isAnagram_bothNull_returnsTrue() {
            assertTrue(StringUtils.isAnagram(null, null));
        }

        @Test
        @DisplayName("isAnagram - 하나만 null이면 false 반환")
        void isAnagram_oneNull_returnsFalse() {
            assertFalse(StringUtils.isAnagram("hello", null));
            assertFalse(StringUtils.isAnagram(null, "hello"));
        }

        @ParameterizedTest
        @CsvSource({
                "listen, silent, true",
                "hello, olleh, true",
                "anagram, nagaram, true",
                "hello, world, false",
                "abc, abcd, false"
        })
        @DisplayName("isAnagram - 애너그램 여부를 확인")
        void isAnagram_validStrings_checksCorrectly(String str1, String str2, boolean expected) {
            assertEquals(expected, StringUtils.isAnagram(str1, str2));
        }

        @Test
        @DisplayName("isAnagram - 공백은 무시하고 비교")
        void isAnagram_ignoresWhitespace() {
            assertTrue(StringUtils.isAnagram("a b c", "cba"));
        }
    }
}
