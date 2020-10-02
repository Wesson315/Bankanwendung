package de.hsw.bankanwendung.dto.minimized;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class KundeMinDto {

    @NonNull
    private Long id;
    private String path;

    public KundeMinDto(@NonNull Long id) {
        this.id = id;
        this.path = "/kunde/" + id;
    }
}