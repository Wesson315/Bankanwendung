package de.hsw.bankanwendung.dto.minimized;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class BuchungMinDto {

    @NonNull
    private Long id;

    private String path;

    public BuchungMinDto(@NonNull Long l) {
        this.id = l;
        this.path = "/buchung/" + l;
    }
}