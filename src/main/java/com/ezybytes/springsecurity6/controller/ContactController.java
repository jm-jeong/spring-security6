package com.ezybytes.springsecurity6.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ezybytes.springsecurity6.model.Contact;
import com.ezybytes.springsecurity6.repository.ContactRepository;

@RestController
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/contact")
    // @PreFilter("filterObject.contactName != 'Test'")//해당 필터 매개변수인 Contact의 contactName 필드가 Test인 애들 제외
    @PostFilter("filterObject.contactName != 'Test'")// 해당 필터는 메소드를 실행시키지만, return에서 권한이 없는 것으로 전달됨.
    public List<Contact> saveContactInquiryDetails(@RequestBody List<Contact> contacts) {
        Contact contact = contacts.get(0);
        contact.setContactId(getServiceReqNumber());
        contact.setCreateDt(new Date(System.currentTimeMillis()));
        contact = contactRepository.save(contact);

        List<Contact> returnedContacts = new ArrayList<>();
        returnedContacts.add(contact);
        return returnedContacts;
    }

    public String getServiceReqNumber() {
        Random random = new Random();
        int ranNum = random.nextInt(999999999 - 9999) + 9999;
        return "SR"+ranNum;
    }
}
