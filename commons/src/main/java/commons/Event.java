package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    @OneToMany(mappedBy = "event", orphanRemoval = true)
    private Set<Expense> expenses;


    @SuppressWarnings("unused")
    public Event() {}

    public Event(String title, Date creationDate) {
        this.title = title;
        this.participants = new HashSet<>();
        this.expenses = new HashSet<>();
        this.id = generateId();
        this.creationDate = creationDate;
        this.lastActivity = LocalDateTime.now();
    }

    static final char[] validCharacters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    // I, 1, O, and 0 omitted for UX (user experience)
    static final int codeLength = 6;
    // 32^6 = 1,073,741,824 combinations

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

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
        this.lastActivity = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void addParticipant(Participant participant){
        participants.add(participant);
        this.lastActivity = LocalDateTime.now();
    }

    public void removeParticipant(Participant participant){
        participants.remove(participant);
        this.lastActivity = LocalDateTime.now();
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void addExpense(Expense expense){
        expenses.add(expense);
        this.lastActivity = LocalDateTime.now();
    }

    public void removeExpense(Expense expense){
        expenses.remove(expense);
        this.lastActivity = LocalDateTime.now();
    }

    public Set<Expense> getExpenses(){
        return expenses;
    }

    public Expense getExpenseByIndex(int index) {
        Iterator<Expense> iterator = expenses.iterator();
        while(iterator.hasNext()) {
            Expense current = iterator.next();
            if(current.getIndex() == index)
                return current;
        }
        return null;
    }

    public LocalDateTime getLastActivity(){
        return lastActivity;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }


}