package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Participant{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @ManyToOne()
    private Event event;
    @OneToMany(mappedBy = "owedTo", orphanRemoval = true)
    private Set<Expense> expensesOwedTo;

    @SuppressWarnings("unused")
    public Participant() {}

    public Participant(String name, Event event) {
        this.name = name;
        this.event = event;
        this.expensesOwedTo = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Event getEvent() {
        return event;
    }

    public void addExpense(Expense expense){
        expensesOwedTo.add(expense);
    }

    public void removeExpense(Expense expense){
        expensesOwedTo.remove(expense);
    }

    public Set<Expense> getExpensesOwedTo() {
        return expensesOwedTo;
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
