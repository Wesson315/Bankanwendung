package de.hsw.bankanwendung.services;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import de.hsw.bankanwendung.beans.Buchung;
import de.hsw.bankanwendung.repositories.BuchungRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BuchungService {
    private BuchungRepository repository;

    @Autowired
    public BuchungService(BuchungRepository repository) {
        this.repository = repository;
    }

    @CachePut(value = "buchung", key = "#result.id")
    @CacheEvict(value = "konto", key = "#b.konto.iban")
    public Buchung saveBuchung(Buchung b) {
        Buchung b1 = repository.save(b);
        log.trace("saveBuchung(" + b.getId() + ") new ID: " + b1.getId());
        return b1;
    }

    @CacheEvict(value = "buchung", key = "#b.id")
    public void deleteBuchung(Buchung b) {
        log.trace("deleteBuchung(" + b.getId() + ")");
        repository.delete(b);
    }

    @CacheEvict(value = "buchung", key = "#b.iban")
    @CachePut(value = "buchung", key = "#result.iban")
    public Buchung updateKonto(Buchung b) {
        log.trace("updateBuchung(" + b.getId() + ")");
        return repository.save(b);
    }

    @Cacheable(value = "buchung", key = "#id")
    public Buchung getBuchung(long id) {
        log.trace("getBuchung(" + id + ")");
        return repository.findById(id).orElseThrow();
    }

    public List<Buchung> findAll() {
        log.trace("getAllBuchungen()");
        return IterableUtils.toList(repository.findAll());
    }
}
