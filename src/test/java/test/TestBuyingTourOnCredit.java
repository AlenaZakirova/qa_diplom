package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import data.SQLHelper;
import page.Element;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static data.SQLHelper.cleanDatabase;

    public class TestBuyingTourOnCredit {
        Element element;
        @BeforeAll
        static void setUpAll() {
            SelenideLogger.addListener("Allure", new AllureSelenide());
        }

        @AfterAll
        static void tearDownAll() {
            SelenideLogger.removeListener("allure");
        }

        @BeforeEach
        void setUp() {
            element = open("http://localhost:8080/", Element.class);
        }

        @AfterEach
        void tearDownAllDatabase() {
            cleanDatabase();
        }

        @Test
        @DisplayName("Sending payment using card No. 1 with valid data.")
        void shouldOfSuccessfulPurchaseTourWithValidCreditCard1() {
            element.chooseByInCredit("Кредит по данным карты");
            element.enteringApprovedCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifySuccessfulNotification("Операция одобрена Банком.");
            var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLineCreditRequestEntity();
            var expectedStatus = "APPROVED";
            assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
        }

        @Test
        @DisplayName("Sending payment using card No. 2 with valid data.")
        void shouldOfSuccessfulPurchaseTourWithValidCreditCard2() {
            element.chooseByInCredit("Кредит по данным карты");
            element.enteringDeclinedCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
            var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
            var expectedStatus = "DECLINED";
            assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
        }

        @Test
        @DisplayName("Sending invalid payment data for card No. 3.")
        void thereMustBeAnUnsuccessfulPurchaseTourWithAnInvalidCreditCard3() {
            element.chooseByInCredit("Кредит по данным карты");
            element.enteringRandomCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        }

    }

