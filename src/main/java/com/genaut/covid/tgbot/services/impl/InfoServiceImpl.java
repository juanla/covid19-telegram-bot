package com.genaut.covid.tgbot.services.impl;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.genaut.covid.tgbot.info.COVIDInfo;
import com.genaut.covid.tgbot.info.COVIDInfoByCountry;
import com.genaut.covid.tgbot.services.InfoService;

/**
 * Servicio que hará del cliente web para consultar a la API de TG.
 * @author Juanla
 *
 */
@Service
public class InfoServiceImpl implements InfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfoServiceImpl.class);

	@Autowired
	private CacheManager cacheManager;
	
	@Value("${covid.info.source}")
	private String covidInfoSource;
	
	@Cacheable("covidInfo")
	@Override
	public COVIDInfo getUpdatedInfo() {
		LOGGER.info("Actualizamos la información del COVID ya que no hay nada en caché");
		try {
			final Document doc = Jsoup.connect(covidInfoSource).get();
			return this.scrapeWebPage(doc);
		} catch (IOException e) {
			LOGGER.error("Error recuperando la información", e);
		}
		return null;
		
	}
	
	/**
	 * Limpiamos la caché cada 30 minutos
	 */
	@Scheduled(fixedRate = 1_800_000)
	public void cleanCovidCache() {
		cacheManager.getCache("covidInfo").invalidate();
	}

	
	/**
	 * Scrapea la información de la web para sacar el resumen 
	 * estadístico del COVID-19
	 * @param doc
	 * @return
	 */
	private COVIDInfo scrapeWebPage(Document doc) {
		final COVIDInfo info = new COVIDInfo();
		
		//Obtenemos información principal
		final Elements mainData = doc.select("div#maincounter-wrap .maincounter-number");
		info.setTotalConfirmed(Long.valueOf(mainData.get(0).text().replace(",", "")));
		info.setTotalDeaths(Long.valueOf(mainData.get(1).text().replace(",", "")));
		info.setTotalRecovered(Long.valueOf(mainData.get(2).text().replace(",", "")));
		info.setUpdateTime(ZonedDateTime.now(ZoneId.of("Europe/Madrid")));
		//Extrae las filas con la información de cada país
		final Elements elementsByCountry = doc.select("#main_table_countries_today tr[style]");
		for(Element e : elementsByCountry) {
			
			//Obtenemos los datos: País, Contagios,Fallecimientos y Recuperados
			final String country = e.child(0).text();
			final Long confirmed = this.parseValueLong(e.child(1).text());
			final Long deaths = this.parseValueLong(e.child(3).text());
			final Long recovered = this.parseValueLong(e.child(5).text());
			//Los guardamos
			final COVIDInfoByCountry infoByCountry = new COVIDInfoByCountry();
			infoByCountry.setCountry(country);
			infoByCountry.setTotalConfirmed(confirmed);
			infoByCountry.setTotalDeaths(deaths);
			infoByCountry.setTotalRecovered(recovered);
			info.getInfoByCountryList().add(infoByCountry);
		}
		return info;
	}
	
	
	private Long parseValueLong(String input) {
		return (input!=null && input.length()>0)?Long.valueOf(input.replace(",", "")):0L;
	}

	
	
}
