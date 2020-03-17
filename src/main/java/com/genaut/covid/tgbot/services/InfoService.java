package com.genaut.covid.tgbot.services;

import com.genaut.covid.tgbot.info.COVIDInfo;

public interface InfoService {

	public COVIDInfo getUpdatedInfo();
	public void cleanCovidCache();

}
