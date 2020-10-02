package de.hsw.bankanwendung.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KundeDto {
    protected long id;
    protected String vorname;
    protected String nachname;
    protected String adresse;
    protected Date geburtsdatum;
}
