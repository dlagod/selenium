package es.ofca.test.psel.common.beans;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import es.ofca.test.psel.common.factory.PropertiesFactory;
import es.ofca.test.psel.common.utils.UsersDataJsonParser;

/**
 * @author dlago
 *
 * Clase que contiene los usuarios de acceso a la aplicación
 */
public class UsersData extends PropertiesFactory implements Serializable {
	
	public static enum Access {
		LIBRE_DISC("Libre-Discapacidad"), PROM_INTER_DISC("Promoción interna-Discapacidad"), PROM_INTER_ORD("Promoción interna-Ordinario"), LIBRE_ORD(
				"Libre-Ordinario");

		private String value = null;

		private Access(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	};
	
	/**
	 * Identificador de la serialización
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(UsersData.class);
	private static final String JSON_FILE = File.separator + "json" + File.separator + "users.json";
	
	/**
	 * Variable de clase singleton
	 */
	private static UsersData instance = null;
    
	/**
	 * Usuarios de acceso a la aplicación clasificador por perfil de acceso
	 */
	private static Map<String, List<UserData>> mapUsers = null;
	
	
	private UsersData(String dirFile) throws IOException {		
		super(dirFile);

		// Inicializar el Mapa de usuarios
		initMap();
	}
	
	/**
	 * Método que inicializa el mapa de usuarios del sistema
	 */
	private void initMap() {
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(reader);
		
		if (jsonElement instanceof JsonArray) {
			mapUsers = UsersDataJsonParser.paserJsonToMap((JsonArray) jsonElement);
		}
	}

	/**
	 * Clase que permite instanciar un fichero de propiedades
	 * @return Devuelve la instancia del fichero de propiedades
	 */
	public static synchronized UsersData getInstance() {
		if (instance == null) {
			try {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("UsersData.getInstance");
				}
				instance = new UsersData(JSON_FILE);
			} catch (IOException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("Se ha producido un error al cargar el fichero de usuarios.", e);
				}
			}
		}
		return instance;
	}
  
	/**
	 * Devuelve un mapa de los usuarios del sistema identificados por perfil de acceso
	 * @return Devuelve un mapa de los usuarios del sistema identificados por perfil de acceso
	 */
	public static Map<String, List<UserData>> getMapUsers() {
		return mapUsers;
	}
}
