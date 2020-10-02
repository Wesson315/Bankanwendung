package de.hsw.bankanwendung.dto;

import de.hsw.bankanwendung.dto.minimized.KundeMinDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KontoIbanSplittedDto {
    private String laendercode;
    private String blz;
    private String kontonummer;
    private double kontostand;
    private KundeMinDto kunde;
}
