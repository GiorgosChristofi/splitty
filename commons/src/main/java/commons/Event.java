package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Entity
public class Event{
    @Id
    private String id;
    private String title;
    private Date creationDate;
    private LocalDateTime lastActivity;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Participant> participants;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"event"})
    private Set<Expense> expenses;


    /***
     * Constructor for object mappers
     */
    @SuppressWarnings("unused")
    public Event() {}

    /***
     * Constructor with a known title and date
     * @param title Event title
     * @param creationDate Event creation date
     */
    public Event(String title, Date creationDate) {
        this.title = title;
        this.participants = new HashSet<>();
        this.expenses = new HashSet<>();
        this.id = generateId();
        this.creationDate = creationDate;
        this.lastActivity = LocalDateTime.now();
    }

    /***
     * Calculates the total spending for the event
     * @return an int of the total cost of the event, in cents
     */
    @JsonIgnore
    public int getTotalSpending(){
        return expenses.stream().mapToInt(Expense::getPriceInCents).sum();
    }

    /***
     * Calculates the total spending per person
     * @return A Map of participants to the amount of expenses attributed to them
     */
    @JsonIgnore
    public HashMap<Participant,Integer> getSpendingPerPerson() {
        HashMap<Participant, Integer> spendingMap = new HashMap<>();

        for (Participant participant:
                participants) {
            spendingMap.put(participant, 0);
        }

        for (Expense expense:
                expenses) {
            Participant spender = expense.getOwedTo();
            Integer balance = spendingMap.get(spender);
            spendingMap.put(spender, (balance+expense.getPriceInCents()));
        }

        return spendingMap;
    }

    private final static int precision = 4;
    private final static long scale = (long) Math.pow(10, precision);
    /**
     * Calculates the share owed to (credit) per Participant for all expenses, split equally
     * @return a Map of participants to the amount they are owed (credit)
     */
    @JsonIgnore
    public HashMap<Participant,Integer> getOwedShares(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();
        BigDecimal initialBalance = BigDecimal.valueOf(0);

        for (Participant participant:
                participants) {
            creditMap.put(participant, initialBalance);
        }

        for (Expense expense:
             expenses) {
            BigInteger costScaled = BigInteger.valueOf(scale * expense.getPriceInCents());
            BigDecimal totalCost = new BigDecimal(costScaled, precision);
            Participant owedTo = expense.getOwedTo();

            BigDecimal owedToBalance = creditMap.get(owedTo);
            creditMap.put(owedTo, (owedToBalance.add(totalCost)));
            splitEqually(creditMap, totalCost);
        }
        return roundMap(creditMap);
    }

    private HashMap<Participant, Integer> roundMap(HashMap<Participant, BigDecimal> creditMap) {
        HashMap<Participant, Integer> roundedMap = new HashMap<>();
        for(Map.Entry<Participant, BigDecimal> entry:
            creditMap.entrySet()){
            Integer roundedValue = entry.getValue().intValue();
            roundedMap.put(entry.getKey(), roundedValue);
        }
        return roundedMap;
    }

    private void splitEqually(HashMap<Participant, BigDecimal> creditMap, BigDecimal totalCost) {
        BigDecimal peopleToDivideBy = BigDecimal.valueOf(participants.size());
        BigDecimal sharePerPerson = totalCost.divide(peopleToDivideBy, RoundingMode.HALF_UP);
        for (Participant participant:
                participants) {
            BigDecimal balance = creditMap.get(participant);
            balance = balance.subtract(sharePerPerson);
            creditMap.put(participant, balance);
        }
    }

    static final char[] validCharacters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    // I, 1, O, and 0 omitted for UX (user experience)
    static final int codeLength = 6;
    // 32^6 = 1,073,741,824 combinations

    /***
     * Generates a random Event ID
     * @return a codeLength length ID consisting of human-readable characters
     */
    public static String generateId(){
        int maxRandomBound = validCharacters.length;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < codeLength; i++){
            int charIndex = random.nextInt(0, maxRandomBound);
            char character = validCharacters[charIndex];
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    /***
     * Getter for Event ID
     * @return Event ID
     */
    public String getId() {
        return id;
    }

    /***
     * Setter for Event title
     * @param title the new Event title
     */
    public void setTitle(String title) {
        this.title = title;
        this.lastActivity = LocalDateTime.now();
    }

    /***
     * Getter for Event title
     * @return Event title
     */
    public String getTitle() {
        return title;
    }

    /***
     * Getter for Event creation date
     * @return Event creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /***
     * Adds a Participant to the Participant set
     * @param participant the Participant to add
     */
    public void addParticipant(Participant participant){
        participants.add(participant);
        this.lastActivity = LocalDateTime.now();
    }

    /***
     * Removes a Participant from the Participant set
     * @param participant the Participant to remove
     */
    public void removeParticipant(Participant participant){
        participants.remove(participant);
        this.lastActivity = LocalDateTime.now();
    }

    /***
     * Getter for Event Participant Set
     * @return Event Participant Set
     */
    public Set<Participant> getParticipants() {
        return participants;
    }

    /***
     * Adds an Expense to the Expense set
     * @param expense the Expense to add
     */
    public void addExpense(Expense expense){
        expenses.add(expense);
        this.lastActivity = LocalDateTime.now();
    }

    /***
     * Removes an Expense from the Expense set
     * @param expense the Expense to remove
     */
    public void removeExpense(Expense expense){
        expenses.remove(expense);
        this.lastActivity = LocalDateTime.now();
    }

    /***
     * Getter for Event Expense Set
     * @return Event Expense Set
     */
    public Set<Expense> getExpenses(){
        return expenses;
    }

    /***
     * Getter for Event last date of activity
     * @return Event last date of activity
     */
    public LocalDateTime getLastActivity(){
        return lastActivity;
    }
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    /***
     * Equals method using EqualsBuilder
     * @param obj the Object to compare equality to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /***
     * HashCode method using HashCodeBuilder
     * @return a hashcode for the Event
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /***
     * String representation method using HashCodeBuilder
     * @return a String representation of the Event
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}