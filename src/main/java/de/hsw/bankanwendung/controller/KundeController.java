package de.hsw.bankanwendung.controller;

import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.bankanwendung.beans.Kunde;
import de.hsw.bankanwendung.dto.KontoDto;
import de.hsw.bankanwendung.dto.KundeDto;
import de.hsw.bankanwendung.dto.KundeMitKontoDto;
import de.hsw.bankanwendung.dto.minimized.KontoMinDto;
import de.hsw.bankanwendung.services.KontoService;
import de.hsw.bankanwendung.services.KundeService;

@RestController
@RequestMapping("/kunde")
public class KundeController {

    private KundeService service;

    @Autowired
    KontoController kontoController;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public KundeController(KundeService service, KontoService kontoService) {
        this.service = service;
    }

    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public List<KundeDto> getKunden(@RequestParam(required = false) boolean kontoFilter) {
        return service.findAll().stream()
                .filter(x -> !kontoFilter || (x.getKonten() != null && !x.getKonten().isEmpty()))
                .map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping(value = "", params = "expand=konto")
    @ResponseStatus(HttpStatus.OK)
    public List<KundeMitKontoDto> getKunden(@RequestParam(required = true) String expand,
            @RequestParam(required = false) boolean kontoFilter) {
        return service.findAll().stream()
                .filter(x -> !kontoFilter || (x.getKonten() != null && !x.getKonten().isEmpty()))
                .map(this::convertToDtoWithKonten).collect(Collectors.toList());
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public KundeDto postKunde(@RequestBody KundeDto kunde) throws ParseException {
        kunde.setId(0);
        Kunde k = this.convertToEntity(kunde);
        return convertToDto(service.saveKunde(k));
    }

    @PutMapping("")
    public KundeDto putKunde(@RequestBody KundeDto kunde, HttpServletResponse response) throws ParseException {

        KundeDto k;
        try {
            service.getKunde(kunde.getId());
            k = patchKunde(kunde);
            response.setStatus(200);
        } catch (NoSuchElementException e) {
            Kunde k2 = service.saveKunde(convertToEntity(kunde));
            k = convertToDto(k2);
            response.setStatus(201);
        }
        return k;
    }

    @PatchMapping("")
    @ResponseStatus(HttpStatus.OK)
    public KundeDto patchKunde(@RequestBody KundeDto kunde) throws ParseException {
        return patchKunde(kunde.getId(), kunde);
    }

    // Single item
    @GetMapping("/{id}")
    public KundeDto getKunde(@PathVariable Long id) {
        return convertToDto(service.getKunde(id));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public KundeDto patchKunde(@PathVariable Long id, @RequestBody KundeDto kunde) throws ParseException {
        if (kunde.getId() <= 0) {
            kunde.setId(id);
        }

        if (id != kunde.getId()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Technische IDs können nicht verändert werden.");
        }

        Kunde k = convertToEntity(kunde);
        if (k.getId() > 0) {
            return convertToDto(service.updateKunde(k));
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Zu ändernde Ressource existiert nicht.");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteKunde(@PathVariable Long id) {
        Kunde k = service.getKunde(id);
        if (k.getKonten() != null && !k.getKonten().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kunde hat noch ein oder mehrere Konten.");
        } else {
            service.deleteKunde(k);
        }
    }

    @GetMapping("/{id}/konten")
    public List<KontoDto> getKundesKonten(@PathVariable Long id) {
        return kontoController.getKonten(id);
    }

    public Kunde getKunde(String stringifyedKunde) {

        throw new NoSuchElementException("Kunden-String enthällt keine ID");
    }

    private KundeDto convertToDto(Kunde kunde) {
        return modelMapper.map(kunde, KundeDto.class);
    }

    private KundeMitKontoDto convertToDtoWithKonten(Kunde kunde) {
        KundeMitKontoDto kundeDto = modelMapper.map(kunde, KundeMitKontoDto.class);
        kundeDto.setKonten(
                kunde.getKonten().stream().map(x -> new KontoMinDto(x.getIban())).toArray(KontoMinDto[]::new));
        return kundeDto;
    }

    private Kunde convertToEntity(KundeDto postDto) throws ParseException {
        return modelMapper.map(postDto, Kunde.class);
    }
}