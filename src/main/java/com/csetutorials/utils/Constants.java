package com.csetutorials.utils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public interface Constants {

	Gson gson = new Gson();

	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

	Type stringListType = new TypeToken<List<String>>() {
	}.getType();

	Type mapStringObjType = new TypeToken<Map<String, Object>>() {
	}.getType();

}
