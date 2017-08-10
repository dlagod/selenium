package es.ofca.test.psel.common.utils;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import es.ofca.test.psel.common.beans.UserData;
/**
 * Bean que serializa el objeto UserData convertiendolo a JSON
 * <br>
 * @author dlago
 */
public class UserDataSerializer implements JsonSerializer<UserData>{


	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(UserData userData, Type type,
			JsonSerializationContext context) {

		// Inicializamos la variable
		JsonObject jsonObject = null;

		if (userData != null) {
			// Simplemente añadimos los elementos que tenemos que codificar.
			jsonObject = new JsonObject();
		
			jsonObject.addProperty("userName", userData.getUserName());
			jsonObject.addProperty("initDate", userData.getInitDate().toString());
			jsonObject.addProperty("endDate", userData.getEndDate().toString());
			jsonObject.addProperty("minAgeDate", userData.getMinAgeDate().toString());
			jsonObject.addProperty("minAge", userData.getMinAge());
			jsonObject.addProperty("callPrice", userData.getCallPrice());
			jsonObject.addProperty("bankAccount", userData.getBankAccount());
			jsonObject.addProperty("bank", userData.getBank());
			jsonObject.addProperty("receiver", userData.getReceiver());
		}

		return jsonObject;
	}
}

