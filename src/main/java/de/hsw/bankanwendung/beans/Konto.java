package de.hsw.bankanwendung.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Konto")
@NoArgsConstructor
public class Konto implements Serializable {
    private static final long serialVersionUID = 726135918670583235L;

    @Id
    private String iban;

    @Column
    private double kontostand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kunde")
    private Kunde kunde;

    @OneToMany(mappedBy = "konto", fetch = FetchType.EAGER)
    private Set<Buchung> buchungen = new HashSet<>();
}
