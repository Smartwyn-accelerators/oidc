package com.fastcode.oidc.domain.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JSONObjectConverter implements AttributeConverter<JSONObject, String> {
    @Override
    public String convertToDatabaseColumn(JSONObject jsonData) {
        String json = "";
        try {
            if(jsonData != null) {
                json = jsonData.toString();
            }
        }
        catch (NullPointerException ex)
        {
            //extend error handling here if you want
           // json = "";
        }
        return json;
    }

    @Override
    public JSONObject convertToEntityAttribute(String jsonDataAsJson) {
    	JSONParser parser = new JSONParser();
		
		Object obj =null;
		try {
		    if(jsonDataAsJson != null) {
                obj = parser.parse(jsonDataAsJson);
            }
			JSONObject jsonData = (JSONObject) obj;
			return jsonData;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			return null;
		}

    }
}
