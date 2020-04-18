package xyz.aymen.newloanquote.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.aymen.newloanquote.api.LoanQuotesApiDelegate;
import xyz.aymen.newloanquote.exception.InsufficientLendersException;
import xyz.aymen.newloanquote.model.ResultDTO;
import xyz.aymen.newloanquote.service.CalculatorService;

import java.math.BigDecimal;

@Service
public class LoanQuotesApiImpl implements LoanQuotesApiDelegate {

    @Autowired
    private CalculatorService calculatorService;

    @Override
    public ResponseEntity<ResultDTO> calculator(BigDecimal loanAmount, MultipartFile file) {
        try {
            return ResponseEntity.ok().body(calculatorService.calculate(file, loanAmount));
        } catch (InsufficientLendersException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

    }
}
