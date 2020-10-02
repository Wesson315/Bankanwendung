package de.hsw.bankanwendung.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IbanServiceTests {
    @Autowired
    private IbanService service;

    private String[] valideIbans = { "DE83500105171613952892", "DE14500105178576264255", "DE43500105175859487442",
            "DE98500105172824583917", "DE77500105175626925256", "GB03BARC20038064351535", // UK
            "SA2412378393286638162244", // Saudi Arabien
            "NL57ABNA6664435071", // Niederlande
            "CH3889144856725431579", // Schweiz
    };
    private String[] invalideIbans = { "D83500105171613952892", "DE53500105171613952892", "DE835001051716139528922",
            "DE83500107171613952892", "83500105171613952892DE", "83500105171613952892", "50010517 1613952892",
            "500 10517 1613952892", "500 105 17 1613952892", "500 105 17 1613 9528 92", "GD03BARC20038064351535",
            "DE83500105171613952892s", "DE835001051716139528922222222222222222222222222", "DE14 5001 0517 8576 2642 55",
            null, "" };

    @Test
    void ibanValidatePositive() {
        for (String iban : valideIbans) {
            assertThat(service.validiere(iban))
                    .withFailMessage("True erwartet, aber False erhalten. Bei Iban: %s", iban).isTrue();
        }

    }

    @Test
    void ibanValidateNegative() {
        for (String iban : invalideIbans) {
            assertThat(service.validiere(iban))
                    .withFailMessage("False erwartet, aber True erhalten. Bei Iban: %s", iban).isFalse();
        }

    }

}
