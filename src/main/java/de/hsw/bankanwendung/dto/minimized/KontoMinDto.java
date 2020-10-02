package de.hsw.bankanwendung.dto.minimized;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class KontoMinDto {
    @NonNull
    private String iban;
    private String path;

    public KontoMinDto(String iban) {
        this.iban = iban;
        this.path = "/konto/" + iban;
    }
}