package client.utils;

import commons.Participant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.transaction.PlatformTransactionManagerCustomizer;
import org.testfx.framework.junit5.ApplicationExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class SettleDebtsUtilsTest {
    @InjectMocks
    SettleDebtsUtils sut;
    @Mock
    ServerUtils server;
    @Mock
    Translation translation;
    @Mock
    EmailHandler emailHandler;
    Participant participant1;
    Participant participant2;
    Participant participant3;

    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }

    @BeforeEach
    void setup(){
        participant1 = new Participant("Vox");
        participant2 = new Participant("Val");
        participant3 = new Participant("Vel");
    }

    BigDecimal bigDecimalize(int number){
        return BigDecimal.valueOf(number);
    }

    /***
     * Randomized transfers result in correct outcome
     */
    @Test
    void randomTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();
        Random rng = new Random();

        Participant participant4 = new Participant("VVV");

        int rand1 = generateRandom(rng);
        int rand2 = generateRandom(rng);
        int rand3 = generateRandom(rng);
        int balancer = -(rand1+rand2+rand3);

        creditMap.put(participant1, bigDecimalize(rand1));
        creditMap.put(participant2, bigDecimalize(rand2));
        creditMap.put(participant3, bigDecimalize(rand3));
        creditMap.put(participant4, bigDecimalize(balancer));

        var initialResult = sut.calculateTransferInstructions(creditMap);
        assertTrue(initialResult.size()<creditMap.size());

        //Run one transfer at a time, and make sure it always settles the debt
        int ceiling = creditMap.size();
        var transferSetAfterTransfer = new HashSet<>(initialResult);
        for (Transfer transfer:
             initialResult) {
            BigDecimal balSender = creditMap.get(transfer.sender());
            BigDecimal balReceiver = creditMap.get(transfer.receiver());
            BigDecimal amount = bigDecimalize(transfer.amount());
            creditMap.put(transfer.sender(), balSender.add(amount));
            creditMap.put(transfer.receiver(), balReceiver.subtract(amount));
            transferSetAfterTransfer.remove(transfer);

            var reranResult = sut.calculateTransferInstructions(creditMap);
            assertEquals(transferSetAfterTransfer, reranResult);

            ceiling -= 1;
            assertTrue(reranResult.size()<ceiling);
        }
    }

    /***
     * Generates a random number between min and max
     */
    private int generateRandom(Random random){
        int min = -1000;
        int max = 1000;
        return random.nextInt(max - min) + min;
    }

    /***
     * One debtor can pay off multiple people
     */
    @Test
    void splitDebtTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        creditMap.put(participant1, bigDecimalize(-25));
        creditMap.put(participant2, bigDecimalize(10));
        creditMap.put(participant3, bigDecimalize(15));

        Transfer transfer1 = new Transfer(participant1, 10, participant2);
        Transfer transfer2 = new Transfer(participant1, 15, participant3);

        var result = sut.calculateTransferInstructions(creditMap);

        assertTrue(result.contains(transfer1));
        assertTrue(result.contains(transfer2));
    }

    /***
     * One creditor can receive from multiple people
     */
    @Test
    void splitCreditTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        creditMap.put(participant1, bigDecimalize(100));
        creditMap.put(participant2, bigDecimalize(-25));
        creditMap.put(participant3, bigDecimalize(-75));

        Transfer transfer1 = new Transfer(participant2, 25, participant1);
        Transfer transfer2 = new Transfer(participant3, 75, participant1);

        var result = sut.calculateTransferInstructions(creditMap);

        assertTrue(result.contains(transfer1));
        assertTrue(result.contains(transfer2));
    }

    /***
     * Net positive amounts are rejected
     */
    @Test
    void netPositiveTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        creditMap.put(participant1, bigDecimalize(100));
        creditMap.put(participant2, bigDecimalize(-10));
        creditMap.put(participant3, bigDecimalize(-20));

        assertThrows(IllegalArgumentException.class, () -> sut.calculateTransferInstructions(creditMap));
    }

    /***
     * Net negative amounts are rejected
     */
    @Test
    void netNegativeTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        creditMap.put(participant1, bigDecimalize(100));
        creditMap.put(participant2, bigDecimalize(-70));
        creditMap.put(participant3, bigDecimalize(-50));

        assertThrows(IllegalArgumentException.class, () -> sut.calculateTransferInstructions(creditMap));
    }

    /***
     * [FEATURE NOT YET IMPLEMENTED]
     * Checks if the Expense generated to settle a normal debt is correct
     */
    @Test
    void createSettlementExpense() {
        Transfer transfer = new Transfer(participant1, 7, participant2);
        var result = sut.createSettlementExpense (transfer);
        assertNotNull(result);
    }

    /***
     * [FEATURE NOT YET IMPLEMENTED]
     * Checks if the Action executed when the Mark Received button is pressed
     * settles a normal debt correctly
     */
    @Test
    void createSettleAction() {
        Transfer transfer = new Transfer(participant1, 7, participant2);
        EventHandler<ActionEvent> result = sut.createSettleAction(transfer, "ABC123");
        result.handle(new ActionEvent());
        assertNotNull(result);
    }

    /***
     * Checks if text generated for a transfer
     * fits the structure and has the correct details
     */
    @Test
    void stringPositive() {
        Participant participantA = new Participant("NameABC");
        Participant participantB = new Participant("NameXYZ");
        Transfer transfer = new Transfer(participantA, 7, participantB);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("senderName", "NameABC");
        expectedValues.put("amount", "0.07\u20ac");
        expectedValues.put("receiverName", "NameXYZ");

        //Only returns this if the expected values are substituted in, null otherwise
        lenient().when(translation.getStringSubstitutionBinding(
                "SettleDebts.String.transferInstructions", expectedValues))
                .thenReturn(stringToObservable(
                        expectedValues.get("senderName") + " gives " +
                        expectedValues.get("amount") + " to " +
                        expectedValues.get("receiverName")));
        String result = sut.createTransferString(transfer).getValue();
        assertTrue(result.contains("NameABC"));
        assertTrue(result.contains("gives"));
        assertTrue(result.contains("0.07"));
        assertTrue(result.contains("to"));
        assertTrue(result.contains("NameXYZ"));
    }

    /***
     * Checks if a negative amount transfer is caught
     */
    @Test
    void stringNegative() {
        Transfer transfer = new Transfer(participant1, -7, participant2);
        assertThrows(IllegalArgumentException.class, () -> sut.createTransferString(transfer));
    }

    /***
     * Checks if a zero amount transfer is caught
     */
    @Test
    void stringZero() {
        Transfer transfer = new Transfer(participant1, 0, participant2);
        assertThrows(IllegalArgumentException.class, () -> sut.createTransferString(transfer));
    }

    /***
     * Checks if a Participant with all bank details
     * has their payment information generated properly
     */
    @Test
    void bankFull(){
        String name = "Vox V.";
        String iban = "NL02ABNA0123456789";
        String bic = "ABNANL2AXXX";

        participant1.setLegalName(name);
        participant1.setIban(iban);
        participant1.setBic(bic);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("holder", name);
        expectedValues.put("iban", iban);
        expectedValues.put("bic", bic);


        //Only returns this if the expected values are substituted in, null otherwise
        lenient().when(translation.getStringBinding("SettleDebts.String.bankAvailable"))
                        .thenReturn(stringToObservable("Bank information available, transfer the money to:"));
        lenient().when(translation.getStringSubstitutionBinding(
                        "SettleDebts.String.bankDetails", expectedValues))
                .thenReturn(stringToObservable(
                                "Account Holder: " + expectedValues.get("holder") +
                                "\nIBAN: " + expectedValues.get("iban") +
                                "\nBIC: " + expectedValues.get("bic")));

        var result = sut.getBankDetails(participant1).getValue();
        assertTrue(result.contains("information available"));
        assertTrue(result.contains(name));
        assertTrue(result.contains(iban));
        assertTrue(result.contains(bic));
    }

    /***
     * Checks if a Participant with missing bank details
     * has their (unavailable) payment information generated properly
     */
    @Test
    void bankMissing(){
        String name = "Vox V.";
        String iban = "";
        String bic = "ABNANL2AXXX";

        participant1.setLegalName(name);
        participant1.setIban(iban);
        participant1.setBic(bic);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("holder", name);
        expectedValues.put("iban", "(MISSING)");
        expectedValues.put("bic", bic);


        //Only returns this if the expected values are substituted in, null otherwise
        lenient().when(translation.getStringBinding("SettleDebts.String.bankUnavailable"))
                .thenReturn(stringToObservable("Full bank information unavailable:"));
        lenient().when(translation.getStringSubstitutionBinding(
                        "SettleDebts.String.bankDetails", expectedValues))
                .thenReturn(stringToObservable(
                        "Account Holder: " + expectedValues.get("holder") +
                                "\nIBAN: " + expectedValues.get("iban") +
                                "\nBIC: " + expectedValues.get("bic")));

        var result = sut.getBankDetails(participant1).getValue();
        assertTrue(result.contains("information unavailable"));
        assertTrue(result.contains(name));
        assertTrue(result.contains("(MISSING)"));
        assertTrue(result.contains(bic));
    }

    @Test
    void generateEmailBodyNoBankCredentials(){
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        Transfer t = new Transfer(sender, 10,receiver);
        String emailBody = sut.generateEmailBody(t);

        assertEquals(emailBody, "Please transfer the amount of " + FormattingUtils.getFormattedPrice(10) + " to Receiver\n\n" +"Thank you!");
    }

    @Test
    void generateEmailBodyWithBankCredentials(){
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        receiver.setBic("BIC");
        receiver.setIban("IBAN");
        receiver.setLegalName("Legal Name");
        Transfer t = new Transfer(sender, 10,receiver);
        String emailBody = sut.generateEmailBody(t);
        assertEquals(emailBody, "Please transfer the amount of " + FormattingUtils.getFormattedPrice(10) + " to Receiver to the following bank account:\n" +
                "\n" +
                "Name: Legal Name\n" +
                "IBAN: IBAN\n" +
                "BIC: BIC\n" +
                "\n" +
                "Thank you!");
    }

    @Test
    void sendEmailTransferTestSuccess(){
        when(emailHandler.sendEmail(any(), any(), any())).thenReturn(true);
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        Transfer t = new Transfer(sender, 10,receiver);
        sut.sendEmailTransferEmail(t);
        waitForJavaFX();
        verify(emailHandler, times(1)).showSuccessPrompt();
    }

    @Test
    void sendEmailTransferTestFail(){
        when(emailHandler.sendEmail(any(), any(), any())).thenReturn(false);
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        Transfer t = new Transfer(sender, 10,receiver);
        sut.sendEmailTransferEmail(t);
        waitForJavaFX();
        verify(emailHandler, times(1)).showFailPrompt();
    }

    private void waitForJavaFX() {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}