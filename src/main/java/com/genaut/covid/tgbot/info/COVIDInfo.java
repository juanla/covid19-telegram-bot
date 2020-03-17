package com.genaut.covid.tgbot.info;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class COVIDInfo {

	//Formatos para el pintado por columnas
	private static final String HEADER_FORMAT = "%-12s %-12s %-12s \n";
	private static final String COUNTRY_COLUMNS_FORMAT = "%-20s %-12s %-12s %-12s \n";

	private ZonedDateTime updateTime;
	private Long totalConfirmed;
	private Long totalDeaths;
	private Long totalRecovered;
	private List<COVIDInfoByCountry> infoByCountryList;
	
	public COVIDInfo() {
		this.infoByCountryList = new ArrayList<>();
	}
	
	/**
	 * Crea el mensaje con el resumen Global. Devolviendolo
	 * en partes para que quepan en un mensaje de Telegram.
	 * @return
	 */
	public List<String> parseResumenGlobal() {
		List<String> messages = new ArrayList<>();
		
		//Añadimos cabecera
		final String totalMessage = "<pre>" +
		String.format(HEADER_FORMAT, "Confirmados", "Fallecidos", "Recuperados")  +
		String.format(HEADER_FORMAT, totalConfirmed, totalDeaths, totalRecovered) +
		"Fecha última actualización: " + updateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString() + 
		"</pre>";
		messages.add(totalMessage);
		
		//Creamos varios mensajes por grupos de paises para que quepan en un único mensaje de chat de TG 
		final String countryHeader = String.format(COUNTRY_COLUMNS_FORMAT, "País", "Confirmados", "Fallecidos", "Recuperados");
		String message = countryHeader;
		Collections.sort(infoByCountryList);
		for(COVIDInfoByCountry countryInfoItem : infoByCountryList) {
			final String currentLine = String.format(COUNTRY_COLUMNS_FORMAT, countryInfoItem.getCountry(), countryInfoItem.getTotalConfirmed(), 
					countryInfoItem.getTotalDeaths(), countryInfoItem.getTotalRecovered());
			
			//Si el contenido no cabe en un único mensaje lo guardamos lo que haya acumulado y creamos otro mensaje
			if((message.length() + currentLine.length() + 10 > 4096)) {
				messages.add("<pre>"+message+"</pre>");
				message = countryHeader;
			}
			message += currentLine;
		}
		messages.add("<pre>"+message+"</pre>");
		
		return messages;
	}
	
	/**
	 * Crea un resumen simple de la situación en España
	 * @return
	 */
	public String parseResumenEs() {
		final Optional<COVIDInfoByCountry> spainInfoOpt = infoByCountryList.stream().filter(c->c.getCountry().equals("Spain")).findFirst();
		if(spainInfoOpt.isPresent()) {
			COVIDInfoByCountry spainInfo = spainInfoOpt.get();
			return 
					"<pre>Información actual <b>España</b>: \n" +
					String.format(HEADER_FORMAT, "Confirmados", "Fallecidos", "Recuperados") +
					String.format(HEADER_FORMAT, spainInfo.getTotalConfirmed(), spainInfo.getTotalDeaths(), spainInfo.getTotalRecovered())
					+"</pre>";	
		} else {
			return "No hay información disponible por el momento";
		}
		
	}
	
	public java.time.ZonedDateTime getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(java.time.ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}
	public Long getTotalConfirmed() {
		return totalConfirmed;
	}
	public void setTotalConfirmed(Long totalConfirmed) {
		this.totalConfirmed = totalConfirmed;
	}
	public Long getTotalDeaths() {
		return totalDeaths;
	}
	public void setTotalDeaths(Long totalDeaths) {
		this.totalDeaths = totalDeaths;
	}
	public Long getTotalRecovered() {
		return totalRecovered;
	}
	public void setTotalRecovered(Long totalRecovered) {
		this.totalRecovered = totalRecovered;
	}
	public List<COVIDInfoByCountry> getInfoByCountryList() {
		return infoByCountryList;
	}
	public void setInfoByCountryList(List<COVIDInfoByCountry> infoByCountryList) {
		this.infoByCountryList = infoByCountryList;
	}
	
}
