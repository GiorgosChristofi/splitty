package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

@Entity
public class Expense{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int index;
    private String name;
    private int priceInCents;
    private Date date;

<<<<<<< HEAD
    @ManyToOne()
    //@JsonIgnore
    private Event event;

=======
>>>>>>> b693a523caaeb29967cfe3f89abe2cd132efb36c
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"expensesOwedTo"})
    private Participant owedTo;
    private boolean toEdit;

    @SuppressWarnings("unused")
    public Expense() {}

    public Expense(String name, int priceInCents, Date date, Participant owedTo) {
        this.name = name;
        this.priceInCents = priceInCents;
        this.date = date;
        this.owedTo = owedTo;
        this.toEdit = false;
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

    public void setPriceInCents(int priceInCents) {
        this.priceInCents = priceInCents;
    }

    public int getPriceInCents() {
        return priceInCents;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setOwedTo(Participant participant){
        this.owedTo = participant;
    }

    public Participant getOwedTo(){
        return owedTo;
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

    public double changeToEuro() {
        return Math.ceil((double)(priceInCents/100));
    }
    public boolean getToEdit() {
        return this.toEdit;
    }
    public void setToEdit(boolean b) {
        this.toEdit = b;
    }
    public int getIndex() {
        return this.index;
    }

    public String eventScreenString() {
        if(owedTo != null)
            return  owedTo.getName() + " paid " + changeToEuro() + " for " + event.getTitle();
        return  "Somebody paid " + changeToEuro() + " for " + event.getTitle();

    }

}
