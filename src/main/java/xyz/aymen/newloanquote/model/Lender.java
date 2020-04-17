package xyz.aymen.newloanquote.model;

import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

/**
 * Bean to represent lenders specified in the input CSV file
 */
public final class Lender implements Comparable<Lender> {
    /**
     * the name of the lender
     */
    @CsvBindByName(column = "Lender", required = true)
    private String name;

    /**
     * the annual interest rate of any loans by this lender
     */
    @CsvBindByName(column = "Rate", required = true)
    private BigDecimal rate;

    /**
     * the total amount available for loans from this lender in pounds sterling
     */
    @CsvBindByName(column = "Available", required = true)
    private int amount;

    /**
     * Default constructor required by bean specification
     */
    public Lender() {
    }

    /**
     * Constructs a lender object with all required properties
     *
     * @param name   the name of the lender
     * @param rate   the annual interest rate of any loans by this lender
     * @param amount the total amount available for loans from this lender in pounds sterling
     */
    public Lender(final String name, final BigDecimal rate, final int amount) {
        this.name = name;
        this.rate = rate;
        this.amount = amount;
    }

    /**
     * Gets the name of the lender
     *
     * @return the name of the lender
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the lender
     *
     * @param name the name of the lender
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the annual interest rate of any loans by this lender
     *
     * @return the annual interest rate of any loans by this lender
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Sets the annual interest rate of any loans by this lender
     *
     * @param rate the annual interest rate of any loans by this lender
     */
    public void setRate(final BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * Gets the total amount available for loans from this lender in pounds sterling
     *
     * @return the total amount available for loans from this lender in pounds sterling
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the total amount available for loans from this lender in pounds sterling
     *
     * @param amount the total amount available for loans from this lender in pounds sterling
     */
    public void setAmount(final int amount) {
        this.amount = amount;
    }

    /**
     * Generated equals method
     *
     * @param obj the referenced object with which to compare
     * @return <code>true</code> if this object is the same as the <code>obj</code> argument; <code>false</code> otherwise.
     */
    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final Lender lender = (Lender) obj;

        if (amount != lender.amount) return false;
        if (!name.equals(lender.name)) return false;
        return rate.equals(lender.rate);
    }

    /**
     * Generated hashCode method
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + rate.hashCode();
        result = 31 * result + amount;
        return result;
    }

    /**
     * Generated toString method
     *
     * @return string representation of this object, for testing purposes.
     */
    @Override
    public String toString() {
        return "Lender{" +
                "name='" + name + '\'' +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }

    @Override
    public int compareTo(Lender o) {
        int rateComparison = this.getRate().compareTo(o.getRate());

        return rateComparison != 0 ? rateComparison : o.getAmount() - this.getAmount();
    }
}
