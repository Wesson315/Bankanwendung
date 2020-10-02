package de.hsw.bankanwendung.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.bankanwendung.beans.Konto;

@Repository
public interface KontoRepository extends CrudRepository<Konto, String> {

}
