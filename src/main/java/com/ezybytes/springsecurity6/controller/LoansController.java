package com.ezybytes.springsecurity6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.ezybytes.springsecurity6.model.Loans;
import com.ezybytes.springsecurity6.repository.LoanRepository;

@RestController
public class LoansController {

    @Autowired
    private LoanRepository loanRepository;

    @PostAuthorize("hasAnyRole('USER')")
    @GetMapping("/myLoans")
    public List<Loans> getLoanDetails(@RequestParam int id) {
        List<Loans> loans = loanRepository.findByCustomerIdOrderByStartDtDesc(id);
        if (loans != null ) {
            return loans;
        }else {
            return null;
        }
    }

}
