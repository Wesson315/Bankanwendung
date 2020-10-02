package de.hsw.bankanwendung.dto;

import de.hsw.bankanwendung.dto.minimized.KontoMinDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KundeMitKontoDto extends KundeDto {
    private KontoMinDto[] konten;
}
