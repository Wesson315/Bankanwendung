package de.hsw.bankanwendung.services;

import org.apache.commons.validator.routines.IBANValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IbanService {

    @Autowired
    public IbanService() {
        //
    }

    public boolean validiere(final String iban) {
        return iban != null ? IBANValidator.getInstance().isValid(iban) : false;
    }

    public String ermittlePruefsumme(final String laendercode, final String blz, final String kontonummer)
            throws CheckDigitException {
        return IBANCheckDigit.IBAN_CHECK_DIGIT.calculate(laendercode + "00" + blz + kontonummer);
    }

    public boolean validiereMitException(final String iban, final RuntimeException exceptionWhenIncorrect) {
        if (validiere(iban)) {
            return true;
        }
        throw exceptionWhenIncorrect;
    }
}
