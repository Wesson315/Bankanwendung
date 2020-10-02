package de.hsw.bankanwendung.dto;

import de.hsw.bankanwendung.dto.minimized.KundeMinDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KontoDto {
    protected String iban;
    protected double kontostand;
    protected KundeMinDto kunde;
}
