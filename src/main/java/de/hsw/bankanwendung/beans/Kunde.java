package de.hsw.bankanwendung.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Kunde")
@NoArgsConstructor
public class Kunde implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String vorname;

    @Column
    private String nachname;

    @Column
    private String adresse;

    @Column
    private java.sql.Date geburtsdatum;

    @OneToMany(mappedBy = "kunde", fetch = FetchType.EAGER)
    private Set<Konto> konten = new HashSet<>();
}
