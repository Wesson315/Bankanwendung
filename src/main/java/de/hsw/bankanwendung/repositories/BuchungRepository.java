package de.hsw.bankanwendung.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.bankanwendung.beans.Buchung;

@Repository
public interface BuchungRepository extends CrudRepository<Buchung, Long> {

}
