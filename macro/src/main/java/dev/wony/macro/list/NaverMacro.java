package dev.wony.macro.list;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * 네이버 자동 구매 매크로
 * <p>
 * Chrome WebDriver를 사용하여 네이버에서 자동 로그인 및 상품 구매를 수행합니다.
 * </p>
 */
public class NaverMacro {

    private static final String NAVER_URL = "https://www.naver.com";
    private static final String LOGIN_BUTTON_CLASS = "link_login";
    private static final String ID_INPUT_SELECTOR = "#id";
    private static final String PW_INPUT_SELECTOR = "#pw";
    private static final String LOGIN_SUBMIT_ID = "log.login";
    private static final String SAVE_BUTTON_ID = "new.save";
    private static final long PURCHASE_WAIT_TIME_MS = 1000L;

    // XPath 상수
    private static final String PRODUCT_SELECT_XPATH = "//*[@id=\"content\"]/div/div[2]/div[2]/fieldset/div[5]/div/a";
    private static final String OPTION_SELECT_XPATH_TEMPLATE = "//*[@id=\"content\"]/div/div[2]/div[2]/fieldset/div[5]/div/ul/li[%d]/a";
    private static final String PURCHASE_BUTTON_XPATH = "//*[@id=\"content\"]/div/div[2]/div[2]/fieldset/div[8]/div[1]/div/a";
    private static final String NORMAL_PAYMENT_XPATH = "//*[@id=\"chargePointScrollArea\"]/div[1]/ul[1]/li[4]/div[1]/span[1]/span";
    private static final String LATER_PAYMENT_XPATH = "//*[@id=\"chargePointScrollArea\"]/div[1]/ul[1]/li[4]/ul/li[3]/span[1]/span";
    private static final String SUBMIT_ORDER_XPATH = "//*[@id=\"orderForm\"]/div/div[7]/button";

    private final String id;
    private final String pw;
    private final String targetUrl;
    private final WebDriver driver;

    /**
     * NaverMacro 생성자 - 기본 ChromeDriver 사용
     *
     * @param id        네이버 아이디
     * @param pw        네이버 비밀번호
     * @param targetUrl 구매할 상품 URL
     */
    public NaverMacro(String id, String pw, String targetUrl) {
        this(id, pw, targetUrl, createDefaultDriver());
    }

    /**
     * NaverMacro 생성자 - 외부 WebDriver 주입 (테스트용)
     *
     * @param id        네이버 아이디
     * @param pw        네이버 비밀번호
     * @param targetUrl 구매할 상품 URL
     * @param driver    WebDriver 인스턴스
     */
    public NaverMacro(String id, String pw, String targetUrl, WebDriver driver) {
        this.id = id;
        this.pw = pw;
        this.targetUrl = targetUrl;
        this.driver = driver;
    }

    /**
     * 기본 Chrome WebDriver를 생성합니다.
     *
     * @return ChromeDriver 인스턴스
     */
    private static ChromeDriver createDefaultDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        return new ChromeDriver(options);
    }

    /**
     * 매크로를 시작합니다.
     * <p>
     * 로그인 후 구매가 성공할 때까지 반복 시도합니다.
     * </p>
     *
     * @param optionIndex 구매할 옵션 번호 (1부터 시작)
     */
    public void start(int optionIndex) {
        navigateToLogin();
        login();
        while (!purchase(optionIndex)) {
            // 구매 성공할 때까지 반복
        }
    }

    /**
     * 네이버 로그인 페이지로 이동합니다.
     */
    private void navigateToLogin() {
        driver.get(NAVER_URL);
        WebElement loginBtn = driver.findElement(By.className(LOGIN_BUTTON_CLASS));
        loginBtn.click();
    }

    /**
     * 네이버에 로그인합니다.
     * <p>
     * JavaScript를 사용하여 ID/PW를 입력합니다 (CAPTCHA 우회 목적).
     * </p>
     */
    private void login() {
        String script = String.format(
                "(function execute(){document.querySelector('%s').value = '%s';document.querySelector('%s').value = '%s';})();",
                ID_INPUT_SELECTOR, id, PW_INPUT_SELECTOR, pw
        );
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(script);
        }

        driver.findElement(By.id(LOGIN_SUBMIT_ID)).click();
        driver.findElement(By.id(SAVE_BUTTON_ID)).click();
    }

    /**
     * 상품을 구매합니다.
     *
     * @param optionIndex 구매할 옵션 번호
     * @return 구매 성공 여부
     */
    private boolean purchase(int optionIndex) {
        try {
            driver.get(targetUrl);

            // 상품 선택 버튼 클릭
            driver.findElement(By.xpath(PRODUCT_SELECT_XPATH)).click();

            // 옵션 선택
            String optionXpath = String.format(OPTION_SELECT_XPATH_TEMPLATE, optionIndex);
            driver.findElement(By.xpath(optionXpath)).click();

            // 구매 클릭
            driver.findElement(By.xpath(PURCHASE_BUTTON_XPATH)).click();

            Thread.sleep(PURCHASE_WAIT_TIME_MS);

            // 일반 결제 클릭
            driver.findElement(By.xpath(NORMAL_PAYMENT_XPATH)).click();

            // 나중에 결제 클릭
            driver.findElement(By.xpath(LATER_PAYMENT_XPATH)).click();

            // 결제 버튼 클릭
            driver.findElement(By.xpath(SUBMIT_ORDER_XPATH)).click();

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("주문 중단됨");
            return false;
        } catch (Exception e) {
            System.out.println("주문 실패");
            return false;
        }
    }

    /**
     * WebDriver를 종료합니다.
     */
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
