package test;

import com.codeborne.selenide.SelenideElement;
import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import page.LoginPage;
import page.VerificationPage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanDatabase;

public class BankLoginTest {
    LoginPage loginPage;
    DataHelper.AuthInfo authInfo;
    VerificationPage verificationPage;
    DataHelper.VerificationCode verificationCode;
    SelenideElement header = $("[data-test-id=dashboard]");

    @AfterAll
    static void teardown() {
        cleanDatabase();
    }

    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    @DisplayName("Successfully login with existing login and password from base")
    void shouldLogin() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("Should get error notification displayed if user absent in base")
    void shouldNotifyErrorIfRandomUserAbsentInBase() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();
    }

    @Test
    @DisplayName(" Should get error notification displayed if valid login and password but random code")
    void shouldNotifyErrorIfValidUserButRandomCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotificationVisibility();
    }

    @Test
    @DisplayName(" Block notification displayed after three invalid logins")
    void shouldNotLoginAfterThreeInvalidLogins() {
        authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();
        authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();
        authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.compareErrorText();
    }

}

