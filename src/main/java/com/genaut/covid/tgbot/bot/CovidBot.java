package com.genaut.covid.tgbot.bot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.genaut.covid.tgbot.ext.Commands;
import com.genaut.covid.tgbot.info.COVIDInfo;
import com.genaut.covid.tgbot.services.InfoService;
@Component
public class CovidBot extends org.telegram.telegrambots.bots.TelegramLongPollingBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(CovidBot.class);

	@Autowired
	private InfoService infoService;
	
	@Value("${telegram.api.url}")
	private String API_URL;
	
	@Value("${telegram.api.token}")
	private String API_TOKEN;
	
	@Value("${telegram.bot.name}")
	private String BOT_USERNAME;

	@Value("${telegram.bot.defaultMessage}")
	private String defaultResponse;
	
	@Value("${telegram.bot.recomendaciones}")
	private String recomendaciones;
	
	@Value("${telegram.bot.informacion}")
	private String informacion;
	
	@Value("${telegram.bot.telefonos}")
	private String telefonos;
	
	@Value("${telegram.bot.inicio}")
	private String inicio;
	
	@Value("${telegram.bot.ayuda}")
	private String ayuda;
	
	@Override
	public void onUpdateReceived(Update update) {
		LOGGER.info("Se ha recibido una update");
		
		final COVIDInfo covidInfo = infoService.getUpdatedInfo();
		
		if (update.hasMessage() && update.getMessage().hasText()) {
			
	        final Long chatId = update.getMessage().getChatId();
	        final String command = update.getMessage().getText();
	        
	        //Obtenemos la respuesta asociada al comando
	        final List<String> responseList = this.getResponse(command, covidInfo);
			
	        for(String response : responseList) {
	        
	        	final SendMessage message = new SendMessage() 
		                .setChatId(chatId)
		                .setParseMode("HTML")
		                .setText(response);
				
		        try {
		            execute(message); //Devolvemos mensaje al usuario
		        } catch (TelegramApiException e) {
		            LOGGER.error("Hubo un problema al formar la respuesta", e);
		        }
		        
	        }
	        
	    }
		
	}

	/**
	 * Dependiendo del evento recibido devolvemos una de las respuestas disponibles
	 * @param command
	 * @param covidInfo
	 * @return
	 */
	private List<String> getResponse(final String command, final COVIDInfo covidInfo) {
		
		LOGGER.debug("Se ha recibido el comando {}", command);
		List<String> response = new ArrayList<>();
		try {
			final String parsedString = command.replace("/", "");
			final Commands commandParsed = Commands.valueOf(parsedString.toUpperCase());
			
			switch(commandParsed) {
				
				case RESUMEN_ES:
					response.add(covidInfo.parseResumenEs());
					break;
				case RESUMEN_GLOBAL:
					response.addAll(covidInfo.parseResumenGlobal());
					break;
				case TELEFONOS:
					response.add(telefonos);
					break;
				case RECOMENDACIONES:
					response.add(recomendaciones);
					break;
				case INFORMACION:
					response.add(informacion);
					break;
				case HELP:
				case AYUDA:
					response.add(ayuda);
					break;
				case START:
				case INICIO:
					response.add(inicio);
					break;
				default:
					response.add(defaultResponse);
					break;
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("Se ha producido un error interpretando el comando", e);
		}

		return response;
	}

	@Override
	public String getBotUsername() {
		return BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return API_TOKEN;
	}

}
