package de.hsw.bankanwendung.dto;

import de.hsw.bankanwendung.dto.minimized.KontoMinDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuchungDto {
    private long id;
    private double betrag;
    private String verwendungszweck;
    private KontoMinDto konto;
}
