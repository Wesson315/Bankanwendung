package de.hsw.bankanwendung.controller;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.bankanwendung.beans.Buchung;
import de.hsw.bankanwendung.beans.Konto;
import de.hsw.bankanwendung.dto.BuchungDto;
import de.hsw.bankanwendung.dto.KontoDto;
import de.hsw.bankanwendung.dto.KontoIbanSplittedDto;
import de.hsw.bankanwendung.dto.KontoMitBuchungDto;
import de.hsw.bankanwendung.dto.KontoMitBuchungMinDto;
import de.hsw.bankanwendung.dto.minimized.BuchungMinDto;
import de.hsw.bankanwendung.dto.minimized.KundeMinDto;
import de.hsw.bankanwendung.services.BuchungService;
import de.hsw.bankanwendung.services.IbanService;
import de.hsw.bankanwendung.services.KontoService;
import de.hsw.bankanwendung.services.KundeService;

@RestController
@RequestMapping("/konto")
public class KontoController {

    private final KontoService service;

    @Autowired
    private KundeService kundeService;

    @Autowired
    private BuchungService buchungService;
    @Autowired
    private BuchungController buchungController;

    @Autowired
    private IbanService ibanService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public KontoController(final KontoService service) {
        this.service = service;
    }

    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public List<KontoDto> getKonten(@RequestParam(required = false) final Long kundeId) {
        return service.findAll().stream().filter(x -> kundeId == null || (x.getKunde().getId() == kundeId))
                .map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping(value = "", params = "expand=buchung")
    @ResponseStatus(HttpStatus.OK)
    public List<KontoMitBuchungDto> getKonten(@RequestParam(required = false) final Long kundeId,
            @RequestParam(required = true) final String expand) {
        return service.findAll().stream().filter(x -> kundeId == null || (x.getKunde().getId() == kundeId))
                .map(this::convertToDtoWithBuchungen).collect(Collectors.toList());
    }

    @GetMapping(value = "/{iban}")
    @ResponseStatus(HttpStatus.OK)
    public KontoDto getKonto(@PathVariable final String iban) {
        try {
            return convertToDto(service.getKonto(iban));
        } catch (final NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kein Konto mit dieser Iban gefunden.", e);
        }
    }

    @GetMapping(value = "/{iban}", params = "expand=buchung")
    @ResponseStatus(HttpStatus.OK)
    public KontoMitBuchungDto getKontoMitBuchungen(@PathVariable final String iban,
            @RequestParam(required = true) final String expand) {
        try {
            return convertToDtoWithBuchungen(service.getKonto(iban));
        } catch (final NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kein Konto mit dieser Iban gefunden.", e);
        }
    }

    @PutMapping(value = "")
    public ResponseEntity<KontoDto> putKonto(@RequestBody final KontoDto kontoDto) throws ParseException {
        ibanService.validiereMitException(kontoDto.getIban(),
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Iban ungültig"));

        Konto newKonto = convertToEntity(kontoDto);

        try {
            final Konto oldKonto = service.getKonto(kontoDto.getIban());
            newKonto = kontoUpdaten(oldKonto, newKonto);
            return new ResponseEntity<KontoDto>(convertToDto(newKonto), HttpStatus.OK);
        } catch (final NoSuchElementException e) {
            newKonto = kontoAnlegen(newKonto);
            return new ResponseEntity<KontoDto>(convertToDto(newKonto), HttpStatus.CREATED);
        }

    }

    @PutMapping(value = "/{iban}")
    public ResponseEntity<KontoDto> putKonto(@PathVariable final String iban, @RequestBody final KontoDto kontoDto)
            throws ParseException {
        final Konto newKonto = convertToEntity(kontoDto);
        if (kontoDto.getIban() == null) {
            newKonto.setIban(iban);
        } else if (!kontoDto.getIban().equals(iban)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Path-Variable (iban) und Iban des Bodys stimmen nicht überein. Technische Ids können nicht geupdatet werden.");
        }
        ibanService.validiereMitException(kontoDto.getIban(),
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Iban ungültig"));

        return putKonto(convertToDto(newKonto));
    }

    @PutMapping(value = "/iban-aufgeschluesselt")
    public ResponseEntity<KontoDto> kontoAnlegenIbanAufgeschluesselt(@RequestBody final KontoIbanSplittedDto kontoDto)
            throws ParseException {

        String pruefziffer;
        try {
            pruefziffer = ibanService.ermittlePruefsumme(kontoDto.getLaendercode(), kontoDto.getBlz(),
                    kontoDto.getKontonummer());

            final String iban = String.format("%s%s%s%s", kontoDto.getLaendercode(), pruefziffer, kontoDto.getBlz(),
                    kontoDto.getKontonummer());

            final KontoDto combinedIbanKontoDto = new KontoDto(iban, kontoDto.getKontostand(), kontoDto.getKunde());

            return putKonto(combinedIbanKontoDto);
        } catch (final CheckDigitException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "IBAN konnte nicht ermittelt werden. Kombination aus Laendercode, BLZ und/oder Kontonummmer invalide.",
                    e);
        }
    }

    @PutMapping(value = "/mit-buchung")
    public KontoMitBuchungMinDto putKontoMitBuchungen(@RequestBody final KontoMitBuchungDto kontoDto,
            final HttpServletResponse response) {

        ibanService.validiereMitException(kontoDto.getIban(),
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Iban ungültig"));

        Konto updatedKonto = convertToEntity(kontoDto);

        final BuchungDto[] buchungen = kontoDto.getBuchungen();

        try {
            // Alte Daten löschen, falls vorhanden

            Konto kOld = service.getKonto(kontoDto.getIban());
            kOld = kontoUpdaten(kOld, updatedKonto);

            // Alle alten Buchungen löschen, da nicht unbedingt alle auf das geupdatete
            // Konto überschrieben werden.
            kOld.getBuchungen().stream().forEach(x -> {
                if (!Stream.of(buchungen).anyMatch(y -> y.getId() == x.getId())) {
                    // Loeschen, da es nicht geupdatet werden wird
                    buchungService.deleteBuchung(x);
                }
            });
            updatedKonto = kOld;
            response.setStatus(HttpStatus.OK.value());
        } catch (final NoSuchElementException e) {
            // Keine alten Daten vorhanden --> Neues Objekt
            response.setStatus(HttpStatus.CREATED.value());
        }
        updatedKonto.setBuchungen(new HashSet<>());
        updatedKonto = service.saveKonto(updatedKonto);

        final Konto k = updatedKonto;
        k.setBuchungen(Stream.of(buchungen).map(x -> new Buchung(x.getId(), k, x.getBetrag(), x.getVerwendungszweck()))
                .map(x -> {
                    Buchung b = this.buchungService.saveBuchung(x);
                    return b;
                }).collect(Collectors.toSet()));

        return convertToDtoWithBuchungenMin(updatedKonto);
    }

    @DeleteMapping("/{iban}")
    public ResponseEntity<String> deleteKonto(@PathVariable final String iban) {
        try {
            final Konto konto = service.getKonto(iban);

            entferneBuchungenVonKonto(konto);

            service.deleteKonto(konto);
            return new ResponseEntity<String>("Success", HttpStatus.NO_CONTENT);
        } catch (final NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Keine Ressource vorhanden", e);
        }
    }

    private void entferneBuchungenVonKonto(final Konto kOld) {
        kOld.getBuchungen().forEach(buchungService::deleteBuchung);
    }

    private Konto kontoAnlegen(final Konto newKonto) {
        return service.saveKonto(newKonto);
    }

    private Konto kontoUpdaten(final Konto oldKonto, final Konto newKonto) {
        oldKonto.setKunde(newKonto.getKunde());
        oldKonto.setKontostand(newKonto.getKontostand());
        return service.updateKonto(oldKonto);
    }

    private KontoDto convertToDto(final Konto konto) {
        final KontoDto kontoDto = modelMapper.map(konto, KontoDto.class);
        kontoDto.setKunde(new KundeMinDto(konto.getKunde().getId()));
        return kontoDto;
    }

    private KontoMitBuchungDto convertToDtoWithBuchungen(final Konto konto) {
        final KontoMitBuchungDto kontoDto = modelMapper.map(konto, KontoMitBuchungDto.class);
        kontoDto.setKunde(new KundeMinDto(konto.getKunde().getId()));
        kontoDto.setBuchungen(buchungController.buchungenOfKonto(kontoDto.getIban()).toArray(BuchungDto[]::new));
        return kontoDto;
    }

    private KontoMitBuchungMinDto convertToDtoWithBuchungenMin(final Konto konto) {
        final KontoMitBuchungMinDto kontoDto = modelMapper.map(konto, KontoMitBuchungMinDto.class);
        kontoDto.setBuchungen(
                konto.getBuchungen().stream().map(x -> new BuchungMinDto(x.getId())).toArray(BuchungMinDto[]::new));
        kontoDto.setKunde(new KundeMinDto(konto.getKunde().getId()));
        return kontoDto;
    }

    private Konto convertToEntity(final KontoDto kontoDto) {
        final Konto k = modelMapper.map(kontoDto, Konto.class);
        if (kontoDto.getKunde() != null && kontoDto.getKunde().getId() > 0) {
            try {
                k.setKunde(kundeService.getKunde(kontoDto.getKunde().getId()));
            } catch (final NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Angegebener Kunde konnte nicht gefunden werden", e);
            }
        } else {
            k.setKunde(null);
        }
        return k;
    }
}