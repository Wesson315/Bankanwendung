package de.hsw.bankanwendung.services;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import de.hsw.bankanwendung.beans.Konto;
import de.hsw.bankanwendung.beans.Kunde;
import de.hsw.bankanwendung.repositories.KontoRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KontoService {
    private KontoRepository repository;

    @Autowired
    public KontoService(KontoRepository repository) {
        this.repository = repository;
    }

    @CachePut(value = "konto", key = "#result.iban")
    public Konto saveKonto(Konto k) {
        Konto k1 = repository.save(k);
        log.trace("saveKonto(" + k.getIban() + ") new ID: " + k1.getIban());
        return k1;
    }

    @CacheEvict(value = "konto", key = "#k.iban")
    public void deleteKonto(Konto k) {
        log.trace("deleteKonto(" + k.getIban() + ")");
        repository.delete(k);
    }

    @CacheEvict(value = "konto", key = "#k.iban")
    @CachePut(value = "konto", key = "#result.iban")
    public Konto updateKonto(Konto k) {
        log.trace("updateKonto(" + k.getIban() + ")");
        return repository.save(k);
    }

    @Cacheable(value = "konto", key = "#iban")
    public Konto getKonto(String iban) {
        log.trace("getKonto(" + iban + ")");
        return repository.findById(iban).orElseThrow();
    }

    public List<Konto> findAll() {
        return IterableUtils.toList(repository.findAll());
    }

    public List<Konto> getKonten(Kunde kunde) {
        log.trace("getAllKonten()");
        return IterableUtils.toList(kunde.getKonten());
    }
}
