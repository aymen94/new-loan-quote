package xyz.aymen.newloanquote.service;

import java.math.BigDecimal;
import java.util.function.Function;

import static java.math.RoundingMode.HALF_UP;

public final class AmortizedLoan {

    /**
     * Used in <code>newtonRaphsonMethod</code> to progress towards zero
     */
    private static final double EPSILON = 0.00001;

    /**
     * The scale used in all BigDecimal calculations
     */
    private static final int SCALE = 10;

    /**
     * Calculates an approximate annual interest rate using only the principal, term and monthly repayment
     * @param principal the initial loan amount
     * @param term number of repayment terms
     * @param monthlyPayment amount of repayment per term
     * @return an approximation of the annual interest rate in decimal format (i.e. 0.1 = 10%)
     */
    public static double getApproximateAnnualInterestRate(final double principal, final int term, final double monthlyPayment) {
        if (principal <= 0) {
            throw new IllegalArgumentException("Principal must be positive");
        } else if (monthlyPayment < principal / term) {
            throw new IllegalArgumentException("Monthly payment not enough to pay off principal in term even without interest");
        } else if (term <= 0) {
            throw new IllegalArgumentException("Term must be positive");
        }

        // a decent guess at the interest rate is to just assume entire monthly payment is interest
        final double guessedMonthlyInterestRate = monthlyPayment / principal;

        // each month, the new amount owed is calculated by multiplying (the amount currently owed (1) + guessedMonthlyInterestRate)
        final double guessedMonthlyMultiplier = 1 + guessedMonthlyInterestRate;

        // use Newton-Raphson method to estimate the monthly multiplier
        final double estimatedMonthlyMultiplier = newtonRaphsonMethod(
                guessedMonthlyMultiplier,

                // this is the function that we want to find roots of
                m -> (principal + monthlyPayment) * Math.pow(m, term) - principal * Math.pow(m, term + 1) - monthlyPayment,

                // this is the derivative of the above
                m -> (principal + monthlyPayment) * term * Math.pow(m, term - 1) - principal * (term + 1) * Math.pow(m, term)
        );

        final double estimatedMonthlyInterestRate = estimatedMonthlyMultiplier - 1;

        // get the estimated annual interest rate
        return 12 * estimatedMonthlyInterestRate;
    }

    /**
     * Uses Newton-Raphson method to find an approximation of a root, given a function and its derivative
     * @param guess the initial guess of the root
     * @param f the function that tends to zero
     * @param fPrime the derivative function of <code>f</code>
     * @return an approximation of the root
     */
    private static double newtonRaphsonMethod(final double guess, final Function<Double, Double> f, final Function<Double, Double> fPrime) {
        double current = guess;

        // only iterate a maximum of 1000 times, it should only iterate more than that if the interest rate is very high
        // but then we don't need that much accuracy
        for (int i = 0; i < 1000 && Math.abs(f.apply(current)) > EPSILON; i++) {
            current = current - f.apply(current) / fPrime.apply(current);
        }

        return current;
    }

    /**
     * Calculates the monthly repayment required using amortized interest
     * @param principal the initial loan amount
     * @param annualInterestRate the annual interest rate in decimal form (i.e. 0.1 = 10%)
     * @param numberOfPaymentPeriods number of repayment periods
     * @return the repayment required to repay capital and interest every month
     */
    public static BigDecimal getMonthlyRepayment(final BigDecimal principal, final BigDecimal annualInterestRate, final int numberOfPaymentPeriods) {
        final int interestRateCompareTo0 = annualInterestRate.compareTo(new BigDecimal(0));

        if (interestRateCompareTo0 < 0) {
            // interest rate is negative
            throw new IllegalArgumentException("Annual interest rate must be non-negative");
        } else if (interestRateCompareTo0 == 0) {
            // interest rate is 0
            return principal.divide(new BigDecimal(numberOfPaymentPeriods), SCALE, HALF_UP);
        } else if (numberOfPaymentPeriods <= 0) {
            throw new IllegalArgumentException("Number of payment periods must be positive");
        }

        final BigDecimal monthlyInterestRate = annualInterestRate.divide(new BigDecimal(12), SCALE, HALF_UP);

        return principal.multiply(monthlyInterestRate)
                .divide(
                        BigDecimal.ONE.subtract(
                                BigDecimal.ONE.divide(
                                        BigDecimal.ONE.add(monthlyInterestRate).pow(numberOfPaymentPeriods),
                                        SCALE, HALF_UP
                                )
                        ),
                        SCALE, HALF_UP
                );
    }
}