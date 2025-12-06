package dev.wony.backendlab.macro;

import dev.wony.backendlab.macro.list.NaverMacro;

/**
 * 매크로 애플리케이션 진입점
 * <p>
 * Chrome WebDriver를 설정하고 NaverMacro를 실행합니다.
 * </p>
 */
public class MacroApplication {

    private static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    private static final String WEB_DRIVER_PATH = "src/main/resources/chromedriver.exe";
    private static final int DEFAULT_OPTION_INDEX = 1;

    static {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
    }

    public static void main(String[] args) {
        String naverId = "";
        String naverPw = "";
        String targetUrl = "";

        NaverMacro macro = new NaverMacro(naverId, naverPw, targetUrl);
        try {
            macro.start(DEFAULT_OPTION_INDEX);
        } finally {
            macro.close();
        }
    }
}
