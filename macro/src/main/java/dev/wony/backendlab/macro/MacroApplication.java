package dev.wony.backendlab.macro;

import lombok.extern.slf4j.Slf4j;

/**
 * 매크로 애플리케이션 진입점
 */
@Slf4j
public class MacroApplication {

    private static final int DEFAULT_INTERVAL_SECONDS = 5;
    private static final int DEFAULT_REPEAT_COUNT = 3;

    /**
     * 애플리케이션 진입점
     *
     * @param args 커맨드 라인 인자
     */
    public static void main(String[] args) {
        log.info("매크로 애플리케이션 시작");

        if (args.length == 0) {
            printUsage();
            return;
        }

        String url = args[0];
        int intervalSeconds = parseInterval(args);
        int repeatCount = parseRepeatCount(args);

        HttpMacro macro = new HttpMacro();
        macro.execute(url, intervalSeconds, repeatCount);

        log.info("매크로 애플리케이션 종료");
    }

    private static int parseInterval(String[] args) {
        if (args.length > 1) {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.warn("인터벌 파싱 실패, 기본값 사용: {}초", DEFAULT_INTERVAL_SECONDS);
            }
        }
        return DEFAULT_INTERVAL_SECONDS;
    }

    private static int parseRepeatCount(String[] args) {
        if (args.length > 2) {
            try {
                return Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                log.warn("반복횟수 파싱 실패, 기본값 사용: {}회", DEFAULT_REPEAT_COUNT);
            }
        }
        return DEFAULT_REPEAT_COUNT;
    }

    private static void printUsage() {
        log.info("사용법: java -jar macro.jar <URL> [인터벌(초)] [반복횟수]");
        log.info("  예시: java -jar macro.jar https://example.com 5 10");
    }
}
