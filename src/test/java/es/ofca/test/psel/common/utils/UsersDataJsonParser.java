package es.ofca.test.psel.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import es.ofca.test.psel.common.beans.UserData;
import es.ofca.test.psel.common.beans.UsersData.Access;

/**
 * Clase que serializa UserData; convierte el Json al mapa de usuarios
 * @author dlago
 *
 */
public final class UsersDataJsonParser {

	/**
	 * El subsistema de logs
	 */
	private static final Logger LOGGER = Logger.getLogger(UsersDataJsonParser.class);

	/**
	 * Método que parsea un Json a un objeto Map
	 * @param jsonElement JSON de entrada
	 * @return Hash de salida
	 */
	public static HashMap<String, List<UserData>> paserJsonToMap(JsonArray jsonElement){
		
		HashMap<String, List<UserData>> mapUsers = new HashMap<String, List<UserData>>();
		
		JsonArray jsonArray = (JsonArray) jsonElement;
		
		for (JsonElement element : jsonArray.getAsJsonArray()) {
		        JsonObject jsonLineItem = (JsonObject) element;
		        
		        // Se crea el Mapa de usuarios
		        if (jsonLineItem.get(Access.LIBRE_DISC.getValue()) != null) {
		        	mapUsers.put(Access.LIBRE_DISC.getValue(), createListUserData(jsonLineItem.get(Access.LIBRE_DISC.getValue())));
		        } else {
		        	  if (jsonLineItem.get(Access.PROM_INTER_DISC.getValue()) != null) {
		        		  mapUsers.put(Access.PROM_INTER_DISC.getValue(), createListUserData(jsonLineItem.get(Access.PROM_INTER_DISC.getValue())));
				      } else {
				    	  if (jsonLineItem.get(Access.PROM_INTER_ORD.getValue()) != null) {
				    		  mapUsers.put(Access.PROM_INTER_ORD.getValue(), createListUserData(jsonLineItem.get(Access.PROM_INTER_ORD.getValue())));
					      } else {
					    	  if (jsonLineItem.get(Access.LIBRE_ORD.getValue()) != null) {
					    		  mapUsers.put(Access.LIBRE_ORD.getValue(), createListUserData(jsonLineItem.get(Access.LIBRE_ORD.getValue())));
						      } else {
						    	  LOGGER.info("No se ha cargado correctamente el siguiente Objecto" + jsonLineItem.toString());
						      }
					      }
				      }
		        }
		}
		
		return mapUsers;
	}
	
	private static List<UserData> createListUserData(JsonElement jsonElement){
		
		List<UserData> listUserData = null;
		
		if (jsonElement != null) {
			listUserData = new ArrayList<UserData>();
			
			if (jsonElement instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray) jsonElement;
				
				for (JsonElement element : jsonArray.getAsJsonArray()) {
					UserData userdata = UserData.fromJSON(element.toString());
					
					listUserData.add(userdata);
				}
			}
		}
		 
		return listUserData;
	}
}
