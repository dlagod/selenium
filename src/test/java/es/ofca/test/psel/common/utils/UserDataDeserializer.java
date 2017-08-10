package es.ofca.test.psel.common.utils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import es.ofca.test.psel.common.beans.UserData;
import es.ofca.test.psel.common.constant.Constants;

/**
 * Clase que deserializa UserData; convierte el Json al Objecto DatosUrgencias
 * @author dlago
 *
 */
public class UserDataDeserializer implements JsonDeserializer<UserData>{
	
	private static final Logger LOGGER = Logger.getLogger(UserDataDeserializer.class.getName());

	/* (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public UserData deserialize(JsonElement input, Type type,
			JsonDeserializationContext context) throws JsonParseException {

		UserData userData = null;
		
		try {
			if (input != null) {
				if(!(input instanceof JsonObject)) throw new JsonParseException(input.getClass().toString());
				JsonObject jsonObject = (JsonObject) input;
				
				String userName = (jsonObject.get("userName") != null ? jsonObject.get("userName").getAsString() : null);
				String initDate = (jsonObject.get("initDate") != null ? jsonObject.get("initDate").getAsString() : null);
				String endDate = (jsonObject.get("endDate") != null ? jsonObject.get("endDate").getAsString() : null);
				String minAgeDate = (jsonObject.get("minAgeDate").isJsonNull()  ? null : jsonObject.get("minAgeDate").getAsString());
				int minAge = (jsonObject.get("minAge").isJsonNull() ? -1: jsonObject.get("minAge").getAsInt());
				double callPrice = (jsonObject.get("callPrice").isJsonNull() ? 0.0 : jsonObject.get("callPrice").getAsDouble());
				String bankAccount = (jsonObject.get("bankAccount").isJsonNull() ? null :  jsonObject.get("bankAccount").getAsString());
				String bank = (jsonObject.get("bank").isJsonNull() ? null : jsonObject.get("bank").getAsString());
				String receiver = (jsonObject.get("receiver") != null ? jsonObject.get("receiver").getAsString() : null);

				//Formatos de Fecha
				DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
				
				//Reservamos memoria
				userData = new UserData();
				
				// Se añaden los atributos
				userData.setUserName(userName);
				
				userData.setInitDate(formatter.parse(initDate));
				userData.setEndDate(formatter.parse(endDate));
				
				if (minAgeDate != null) {
					userData.setMinAgeDate(formatter.parse(minAgeDate));
				}
				
				userData.setMinAge(minAge);
				userData.setCallPrice(callPrice);
				userData.setBankAccount(bankAccount);
				userData.setBank(bank);
				userData.setReceiver(receiver);
			}
		} catch (ParseException e) {
			LOGGER.error("Se ha producido un error al pasear el JSON", e);
			throw new JsonParseException(e);
		}

		return userData;
	}
}
