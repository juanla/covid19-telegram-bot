package com.genaut.covid.tgbot.info;

/**
 * Agrupa la información de las estadísticas por país
 * @author Juanla
 *
 */
public class COVIDInfoByCountry implements Comparable<COVIDInfoByCountry> {

	private String country;
	private Long totalConfirmed;
	private Long totalDeaths;
	private Long totalRecovered;
	
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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public int compareTo(COVIDInfoByCountry o) {
		return o.getTotalConfirmed().compareTo(this.getTotalConfirmed());
	}
	
	
}
