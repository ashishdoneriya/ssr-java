package com.csetutorials.ssj.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Constants {

	Gson gson = new Gson();

	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

}
