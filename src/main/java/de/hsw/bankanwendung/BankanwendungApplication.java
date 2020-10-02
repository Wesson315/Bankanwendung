package de.hsw.bankanwendung;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import de.hsw.bankanwendung.beans.Buchung;
import de.hsw.bankanwendung.beans.Konto;
import de.hsw.bankanwendung.beans.Kunde;
import de.hsw.bankanwendung.services.BuchungService;
import de.hsw.bankanwendung.services.KontoService;
import de.hsw.bankanwendung.services.KundeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableCaching
public class BankanwendungApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankanwendungApplication.class, args);
	}

	@Component
	public final class RedisRunner implements CommandLineRunner {
		private KundeService kundeService;
		private KontoService kontoService;
		private BuchungService buchungService;

		@Autowired
		public RedisRunner(KundeService service, KontoService kontoService, BuchungService buchungService) {
			this.kundeService = service;
			this.kontoService = kontoService;
			this.buchungService = buchungService;
		}


		@Override
		public void run(String... args) throws Exception {
			// Leert den REDIS-Cache
			log.info("Clearing REDIS cache");


			log.info("Successfully cleard REDIS cache");

			log.info("Start initializing database");

			log.info("api documentation avaliable at http://localhost:1338/api.html");

			Kunde kunde;
			Konto konto;
			Buchung buchung;

			kunde = new Kunde();
			kunde.setVorname("Hans");
			kunde.setNachname("Heinrichsen");
			kunde.setAdresse("Weg 2");
			kunde.setGeburtsdatum(Date.valueOf("2000-09-30"));
			kundeService.saveKunde(kunde);

			kunde = new Kunde();
			kunde.setVorname("Dieter");
			kunde.setNachname("Schnürschuh");
			kunde.setAdresse("Straße 5");
			kunde.setGeburtsdatum(Date.valueOf("1970-01-05"));
			kundeService.saveKunde(kunde);

			kunde = kundeService.getKunde(1);

			konto = new Konto();
			konto.setIban("DE52500105179745278665");
			konto.setKontostand(100.11);
			konto.setKunde(kunde);
			kontoService.saveKonto(konto);

			konto = new Konto();
			konto.setIban("BH91QXJG57416256338363");
			konto.setKontostand(5615.60);
			konto.setKunde(kunde);
			kontoService.saveKonto(konto);

			konto = kontoService.getKonto("DE52500105179745278665");

			buchung = new Buchung();
			buchung.setKonto(konto);
			buchung.setBetrag(1000.00);
			buchung.setVerwendungszweck("Zubuchung");
			buchungService.saveBuchung(buchung);

			buchung = new Buchung();
			buchung.setKonto(konto);
			buchung.setBetrag(-500.00);
			buchung.setVerwendungszweck("Abbuchung");
			buchungService.saveBuchung(buchung);

			konto = kontoService.getKonto("BH91QXJG57416256338363");

			buchung = new Buchung();
			buchung.setKonto(konto);
			buchung.setBetrag(10000000.00);
			buchung.setVerwendungszweck("Shady Öhlshaich geschmiert");
			buchungService.saveBuchung(buchung);

			buchung = new Buchung();
			buchung.setKonto(konto);
			buchung.setBetrag(-499999.99);
			buchung.setVerwendungszweck("Lambo gekauft");
			buchungService.saveBuchung(buchung);

			log.info("Succesfully initialized database");
		}
	}
}
