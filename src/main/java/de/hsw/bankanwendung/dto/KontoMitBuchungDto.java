package de.hsw.bankanwendung.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KontoMitBuchungDto extends KontoDto {
    private BuchungDto[] buchungen;
}