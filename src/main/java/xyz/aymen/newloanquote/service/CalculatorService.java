package xyz.aymen.newloanquote.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.aymen.newloanquote.exception.InsufficientLendersException;
import xyz.aymen.newloanquote.model.Lender;
import xyz.aymen.newloanquote.model.ResultDTO;
import xyz.aymen.newloanquote.util.FileUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CalculatorService {

    public ResultDTO calculate(MultipartFile data, BigDecimal loanAmount) throws InsufficientLendersException {
        try {
            List<Lender> lenderList = FileUtils.convertFileToModel(data.getBytes());
            LoanQuoteCalculator loanQuoteCalculator = new LoanQuoteCalculator(lenderList);
            return loanQuoteCalculator.getQuote(loanAmount);
        } catch (IOException e) {
            return null;
        }
    }

}
