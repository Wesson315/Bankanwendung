package de.hsw.bankanwendung.controller;

import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.bankanwendung.services.IbanService;

@RestController
@RequestMapping("/iban")
public class IbanController {

    private IbanService service;

    @Autowired
    public IbanController(IbanService service) {
        this.service = service;
    }

    @GetMapping(path = "/validiere")
    public boolean validiere(@RequestParam String iban) {
        return service.validiere(iban);
    }

    @GetMapping(path = "/berechne-pruefsumme")
    public String ermittlePruefsumme(@RequestParam String bankleitzahl, @RequestParam String kontonummer,
            @RequestParam String laendercode) {
        try {
            return service.ermittlePruefsumme(bankleitzahl, kontonummer, laendercode);
        } catch (CheckDigitException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bestimmung der Pruefsumme war nicht moeglich.",
                    e);
        }
    }

}