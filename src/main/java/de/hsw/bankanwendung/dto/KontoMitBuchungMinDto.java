package de.hsw.bankanwendung.dto;

import de.hsw.bankanwendung.dto.minimized.BuchungMinDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KontoMitBuchungMinDto extends KontoDto {
    private BuchungMinDto[] buchungen;
}
