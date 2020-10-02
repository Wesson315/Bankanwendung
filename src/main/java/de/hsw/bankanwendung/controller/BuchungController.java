package de.hsw.bankanwendung.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.bankanwendung.beans.Buchung;
import de.hsw.bankanwendung.dto.BuchungDto;
import de.hsw.bankanwendung.dto.KontoDto;
import de.hsw.bankanwendung.dto.minimized.KontoMinDto;
import de.hsw.bankanwendung.services.BuchungService;
import de.hsw.bankanwendung.services.KontoService;

@RestController
@RequestMapping("/buchung")
public class BuchungController {
    private BuchungService service;

    @Autowired
    private KontoService kontoService;

    @Autowired
    private KontoController kontoController;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public BuchungController(BuchungService service, KontoService kontoservice) {
        this.service = service;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<BuchungDto> getAll() {
        return service.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping(value = "", params = "iban")
    @ResponseStatus(HttpStatus.OK)
    public List<BuchungDto> buchungenOfKonto(@RequestParam String iban) {
        try {
            return kontoService.getKonto(iban).getBuchungen().stream().map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Angegebenes Konto konnte nicht gefunden werden",
                    e);
        }
    }

    @GetMapping("/{id}")
    public BuchungDto findById(@PathVariable Long id) {
        try {
            return convertToDto(service.getBuchung(id));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Angegebenes Konto konnte nicht gefunden werden",
                    e);
        }
    }

    @GetMapping("/{id}/konto")
    public KontoDto kontoOfBuchung(@PathVariable Long id) {
        try {
            return kontoController.getKontoMitBuchungen(service.getBuchung(id).getKonto().getIban(), "buchungen");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Angegebenes Konto konnte nicht gefunden werden",
                    e);
        }
    }

    @PostMapping("")
    public BuchungDto addBuchung(@RequestBody BuchungDto buchungDto) {
        try {
            Buchung buchung = convertToEntity(buchungDto);
            buchung.setId(0);
            return convertToDto(service.saveBuchung(buchung));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Angegebenes Konto konnte nicht gefunden werden",
                    e);
        }
    }

    @PatchMapping("")
    public BuchungDto updateBuchung(@RequestBody BuchungDto buchungDto) {
        Buchung buchung = convertToEntity(buchungDto);
        if (buchung.getId() > 0) {
            return convertToDto(service.saveBuchung(buchung));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Buchung zeigt auf keine valide ID.");
    }

    private BuchungDto convertToDto(Buchung konto) {
        BuchungDto kontoDto = modelMapper.map(konto, BuchungDto.class);
        kontoDto.setKonto(new KontoMinDto(konto.getKonto().getIban()));
        return kontoDto;
    }

    private Buchung convertToEntity(BuchungDto kontoDto) {
        Buchung k = modelMapper.map(kontoDto, Buchung.class);
        if (kontoDto.getKonto() != null) {
            try {
                k.setKonto(kontoService.getKonto(kontoDto.getKonto().getIban()));
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Angegebenes Konto konnte nicht gefunden werden", e);
            }
        } else {
            k.setKonto(null);
        }
        return k;
    }

}