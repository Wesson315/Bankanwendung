DROP DATABASE IF EXISTS Bankanwendung;
SET default_storage_engine = InnoDB;
CREATE schema Bankanwendung DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Bankanwendung;
DROP USER IF EXISTS 'Baenker';
CREATE USER 'Baenker' @'%' IDENTIFIED BY 'SuperSecure1337';
GRANT INSERT,
    UPDATE,
    DELETE,
    SELECT ON `Bankanwendung`.* TO 'Baenker' @'%';
DROP TABLE IF EXISTS buchungen,
konto,
kunde;
CREATE TABLE kunde (
    id int not null AUTO_INCREMENT,
    nachname varchar(64),
    vorname varchar(64),
    geburtsdatum Date,
    adresse varchar(200),
    PRIMARY KEY (id)
);
CREATE TABLE konto (
    iban varchar(22),
    kontostand double,
    kunde int,
    PRIMARY KEY (iban),
    FOREIGN KEY (kunde) REFERENCES kunde(id)
);
CREATE TABLE buchung (
    id int not null AUTO_INCREMENT,
    konto varchar(22),
    betrag double,
    verwendungszweck varchar(140),
    PRIMARY KEY (id),
    FOREIGN KEY (konto) REFERENCES konto(iban)
);