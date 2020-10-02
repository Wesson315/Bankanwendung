package de.hsw.bankanwendung.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class UeberweisungDto {
    @NonNull
    String ibanSender;
    @NonNull
    String ibanEmpfaenger;
    @NonNull
    Double betrag;
    String verwendungszweck;
}
