package de.hsw.bankanwendung.controller;

import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.bankanwendung.beans.Buchung;
import de.hsw.bankanwendung.beans.Konto;
import de.hsw.bankanwendung.dto.BuchungDto;
import de.hsw.bankanwendung.dto.UeberweisungDto;
import de.hsw.bankanwendung.dto.minimized.KontoMinDto;
import de.hsw.bankanwendung.services.BuchungService;
import de.hsw.bankanwendung.services.IbanService;
import de.hsw.bankanwendung.services.KontoService;

@RestController
public class UeberweisungController {

    @Autowired
    KontoService kontoService;

    @Autowired
    IbanService ibanService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    BuchungService buchungService;

    @PostMapping("/ueberweisung")
    public BuchungDto[] fuehreUeberweisungAus(@RequestBody UeberweisungDto ueberweisung) {
        double betrag = ueberweisung.getBetrag();

        if (betrag == 0) {
            return new BuchungDto[0];
        }

        ibanService.validiereMitException(ueberweisung.getIbanSender(),
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sender-Iban ist nicht valide."));
        ibanService.validiereMitException(ueberweisung.getIbanEmpfaenger(),
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empfaenger-Iban ist nicht valide."));

        int count = 0;

        Konto sender = null;
        try {
            sender = kontoService.getKonto(ueberweisung.getIbanSender());
            count++;
        } catch (NoSuchElementException e) {
            // Sender nicht in Datenbank
        }

        Konto empfaenger = null;
        try {
            empfaenger = kontoService.getKonto(ueberweisung.getIbanEmpfaenger());
            count++;
        } catch (NoSuchElementException e) {
            // Empfaenger nicht in Datenbank
        }

        String verwendungszweck = ueberweisung.getVerwendungszweck();
        if (verwendungszweck == null) {
            verwendungszweck = "";
        }

        BuchungDto[] rueckgabe = new BuchungDto[count];

        if (sender != null) {
            Buchung buchung = new Buchung(0, sender, -1 * betrag,
                    verwendungszweck + " (" + ueberweisung.getIbanEmpfaenger() + ")");
            rueckgabe[0] = convertToDto(buchungService.saveBuchung(buchung));
        }

        if (empfaenger != null) {
            Buchung buchung = new Buchung(0, empfaenger, betrag,
                    verwendungszweck + " (" + ueberweisung.getIbanSender() + ")");
            rueckgabe[count - 1] = convertToDto(buchungService.saveBuchung(buchung));
        }

        return rueckgabe;
    }

    private BuchungDto convertToDto(Buchung konto) {
        BuchungDto kontoDto = modelMapper.map(konto, BuchungDto.class);
        kontoDto.setKonto(new KontoMinDto(konto.getKonto().getIban()));
        return kontoDto;
    }
}
