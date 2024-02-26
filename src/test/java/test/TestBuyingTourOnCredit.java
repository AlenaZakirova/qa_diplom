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
        void mustHaveHadSuccessfulPurchaseTourWithValidCreditCard1() {
            element.chooseBy("Оплата по карте");
            element.enteringApprovedCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifySuccessfulNotification("Операция одобрена Банком.");
            var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
            var expectedStatus = "APPROVED";
            assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
        }

        @Test
        @DisplayName("Sending payment using card No. 2 with valid data.")
        void mustHaveHadSuccessfulPurchaseTourWithValidCreditCard2() {
            element.chooseBy("Оплата по карте");
            element.enteringDeclinedCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
            var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
            var expectedStatus = "DECLINED";
            assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
        }

        @Test
        @DisplayName("Sending invalid payment data for card No. 3.")
        void thereMustBeAnUnsuccessfulPurchaseTourWithAnInvalidCreditCard3() {
            element.chooseBy("Оплата по карте");
            element.enteringRandomCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        }

        @Test
        @DisplayName("Submitting a form with an empty value.")
        void shouldReturnErrorWhenEmptyForm() {
            element.chooseBy("Оплата по карте");
            element.verifySuccessfulNotificationIsNotVisible();
            element.verifyErrorCardNumberField("Неверный формат");
            element.verifyErrorMonthField("Неверный формат");
            element.verifyErrorYearField("Неверный формат");
            element.verifyErrorOwnerField("Поле обязательно для заполнения");
            element.verifyErrorCVCField("Неверный формат");
        }

        @Test
        @DisplayName("Error when filling out a form with expired card data.")
        void shouldReturnAnErrorWithExpiredcreditCardInformation() {
            element.chooseBy("Оплата по карте");
            element.enteringApprovedCard();
            element.enteringInvalidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifySuccessfulNotificationIsNotVisible();
            element.verifyPeriodErrorYearField("Истёк срок действия карты");
        }

        @Test
        @DisplayName("Submitting a form with an empty card value.")
        void shouldReturnAnErrorIfTheСreditCardNumberIsEmpty() {
            element.chooseBy("Оплата по карте");
            element.enteringInvalidCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringValidCVC();
            element.verifySuccessfulNotificationIsNotVisible();
            element.verifyErrorCardNumberField("Неверный формат");
        }



        @Test
        @DisplayName("Error when buying a tour with invalid cardholder data on the form.")
        void shouldReturnAnErrorWhenCardWithAnInvalidCreditCardHolder() {
            element.chooseBy("Оплата по карте");
            element.enteringApprovedCard();
            element.enteringValidCardValidityPeriod();
            element.enteringInValidOwner();
            element.enteringValidCVC();
            element.verifySuccessfulNotificationIsNotVisible();
            element.verifyErrorOwnerField("Поле обязательно для заполнения");
        }

        @Test
        @DisplayName("Checking the CVC/CVV field with invalid data.")
        void shouldReturnErrorIfTheCreditCardHasAnInvalidCVC() {
            element.chooseBy("Оплата по карте");
            element.enteringApprovedCard();
            element.enteringValidCardValidityPeriod();
            element.enteringValidOwner();
            element.enteringInValidCVC();
            element.verifySuccessfulNotificationIsNotVisible();
            element.verifyErrorCVCField("Неверный формат");
        }


    }

