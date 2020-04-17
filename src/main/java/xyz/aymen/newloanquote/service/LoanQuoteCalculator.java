package xyz.aymen.newloanquote.service;

import xyz.aymen.newloanquote.exception.InsufficientLendersException;
import xyz.aymen.newloanquote.model.Lender;
import xyz.aymen.newloanquote.model.ResultDTO;

import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ROUND_UP;
import static xyz.aymen.newloanquote.constant.LoanQuote.REPAYMENT_MONTHS;

public class LoanQuoteCalculator {

    private final List<Lender> lenders;

    public LoanQuoteCalculator(final List<Lender> lenders) {
        // sort lender based on cheapest rate and the largest amount
        this.lenders = lenders;
        Collections.sort(lenders);
    }

    public Collection<Lender> getLenders() {
        return lenders;
    }

    public ResultDTO getQuote(BigDecimal loanAmount) throws InsufficientLendersException {
        final Map<Lender, Integer> loans = getLendersForLoan(loanAmount.intValue());

        // calculate monthly repayment for each individual lender
        final BigDecimal monthlyRepayment = loans.entrySet().stream()

                // calculate total monthly repayment by calculating monthly repayment towards each individual lender
                .map(individualLoan -> {
                    final Lender lender = individualLoan.getKey();
                    final Integer individualLoanAmount = individualLoan.getValue();

                    return getMonthlyRepayment(lender.getRate(), individualLoanAmount);
                })

                // add up each monthly repayment
                .reduce(BigDecimal::add)

                // there must be at least one lender, so this is impossible
                .orElseThrow(() -> new IllegalStateException("getLendersForLoan should never return empty map"));

        // calculate total repayment based on non-rounded monthly repayment
        final BigDecimal totalRepayment = monthlyRepayment.multiply(new BigDecimal(REPAYMENT_MONTHS));

        // estimate interest rate based on monthly repayment
        final double rate = getApproximateAnnualInterestRate(loanAmount.intValue(), monthlyRepayment);
        ResultDTO result = new ResultDTO();
        result.setRequestedAmount(loanAmount);
        result.setRate(new BigDecimal(rate).setScale(1, ROUND_HALF_UP));
        result.setMonthlyRepayment(monthlyRepayment.setScale(2, ROUND_HALF_UP));
        result.setTotalRepayment(totalRepayment.setScale(2, ROUND_UP));
        return result;
    }

    /**
     * Calculates an approximate annual interest rate using only the principal, monthly repayment
     *
     * @param loanAmount       initial loan amount
     * @param monthlyRepayment amount of repayment per month
     * @return an approximation of the annual interest rate in percentage format
     */
    double getApproximateAnnualInterestRate(final int loanAmount, final BigDecimal monthlyRepayment) {
        return AmortizedLoan.getApproximateAnnualInterestRate(loanAmount, REPAYMENT_MONTHS, monthlyRepayment.doubleValue()) * 100;
    }

    /**
     * Calculates the monthly repayment required using amortized interest
     *
     * @param rate                 annual interest rate of the loan
     * @param individualLoanAmount the initial loan amount
     * @return the repayment required to repay capital and interest every month
     */
    BigDecimal getMonthlyRepayment(final BigDecimal rate, final Integer individualLoanAmount) {
        return AmortizedLoan.getMonthlyRepayment(new BigDecimal(individualLoanAmount), rate, REPAYMENT_MONTHS);
    }

    /**
     * Retrieves a list of lender and loan amount pairs that represent how much the borrower is borrowing from each lender
     *
     * @param loanAmount the total loan amount requested
     * @return list of lender and loan amount pairs that represent how much the borrower is borrowing from each lender
     * @throws InsufficientLendersException thrown when there is not sufficient funding from the list of lenders
     *                                      to satisfy the requested loan amount
     */
    Map<Lender, Integer> getLendersForLoan(final int loanAmount) throws InsufficientLendersException {
        final Map<Lender, Integer> result = new HashMap<>();

        int remainingLoanAmount = loanAmount;

        for (final Lender lender : lenders) {
            // can this lender satisfy remaining loan required?
            if (lender.getAmount() >= remainingLoanAmount) {
                result.put(lender, remainingLoanAmount);

                return result;
            }

            // use up all of lender's quota
            result.put(lender, lender.getAmount());

            remainingLoanAmount -= lender.getAmount();
        }

        throw new InsufficientLendersException();
    }
}
