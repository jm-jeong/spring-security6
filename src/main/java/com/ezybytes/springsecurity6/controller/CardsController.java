package com.ezybytes.springsecurity6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.ezybytes.springsecurity6.model.Cards;
import com.ezybytes.springsecurity6.model.Customer;
import com.ezybytes.springsecurity6.repository.CardsRepository;
import com.ezybytes.springsecurity6.repository.CustomerRepository;

@RestController
public class CardsController {

    @Autowired
    private CardsRepository cardsRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/myCards")
    public List<Cards> getCardDetails(@RequestParam String email) {
        List<Customer> customers = customerRepository.findByEmail(email);
        if (customers != null && !customers.isEmpty()) {
            List<Cards> cards = cardsRepository.findByCustomerId(customers.get(0).getId());
            if (cards != null ) {
                return cards;
            }
        }
        return null;
    }

}
