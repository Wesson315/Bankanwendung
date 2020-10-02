package de.hsw.bankanwendung.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.bankanwendung.beans.Kunde;

@Repository
public interface KundeRepository extends CrudRepository<Kunde, Long> {

}
