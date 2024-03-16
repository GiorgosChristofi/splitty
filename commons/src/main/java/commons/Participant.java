package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owedTo", orphanRemoval = true)
    @JsonIgnoreProperties(value = {"owedTo"})
    private Set<Expense> expensesOwedTo;

    @SuppressWarnings("unused")
    public Participant() {}

    public Participant(String name) {
        this.name = name;
        this.expensesOwedTo = new HashSet<>();
    }

    /**
     * Constructor used for testing where ID's need to be set.
     * @param id ID of the participant
     * @param name Name of the participant
     */
    public Participant(long id, String name) {
        this.id = id;
        this.name = name;
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
