package de.hsw.bankanwendung.services;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import de.hsw.bankanwendung.beans.Kunde;
import de.hsw.bankanwendung.repositories.KundeRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KundeService {

    private KundeRepository repository;

    @Autowired
    public KundeService(KundeRepository jpaRepository) {
        this.repository = jpaRepository;
    }

    @CachePut(value = "kunde", key = "#result.id")
    public Kunde saveKunde(Kunde k) {
        Kunde k1 = repository.save(k);
        log.trace("saveKunde(" + k1.getId() + ") new ID: " + k1.getId());
        return k1;
    }

    @CacheEvict(value = "kunde", key = "#k.id")
    public void deleteKunde(Kunde k) {
        log.trace("deleteKunde(" + k.getId() + ")");
        repository.delete(k);
    }

    @CacheEvict(value = "kunde", key = "#k.id")
    @CachePut(value = "kunde", key = "#result.id")
    public Kunde updateKunde(Kunde k) {
        log.trace("updateKunde(" + k.getId() + ")");
        return repository.save(k);
    }

    @Cacheable(value = "kunde", key = "#id")
    public Kunde getKunde(long id) {
        log.trace("getKunde(" + id + ")");
        return repository.findById(id).orElseThrow();
    }

    public List<Kunde> findAll() {
        log.trace("getAllKunden()");
        return IterableUtils.toList(repository.findAll());
    }
}
