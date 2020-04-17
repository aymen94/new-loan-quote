package xyz.aymen.newloanquote.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanQuote {
    public final static int MIN_LOAN_AMOUNT = 1000;
    public final static int MAX_LOAN_AMOUNT = 15000;
    public final static int LOAN_AMOUNT_INCREMENT = 100;
    public final static int REPAYMENT_MONTHS = 36;
}
