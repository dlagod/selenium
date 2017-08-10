package es.ofca.test.psel.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import es.ofca.test.psel.common.factory.PropertiesFactory;

/**
 * Clase que permite leer un fichero de configuración
 * 
 * @author dlago
 *
 */
public class ConfigFile extends PropertiesFactory {
	
	private static final Logger LOGGER = Logger.getLogger(ConfigFile.class);
	private static final String CONFIG_FILE = File.separator + "config" + File.separator + "config.properties";
	
	private static ConfigFile instance = null;
	private static Properties properties = null;

	private ConfigFile(String dirFile) throws IOException {		
		super(dirFile);
		properties = new Properties();
		properties.load(reader);
	}

	/**
	 * Clase que permite instanciar un fichero de propiedades
	 * @return Devuelve la instancia del fichero de propiedades
	 */
	public static synchronized ConfigFile getInstance() {
		if (instance == null) {
			try {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("ConfigFile.getInstance");
				}
				instance = new ConfigFile(CONFIG_FILE);
			} catch (IOException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("Se ha producido un error al cargar el fichero de configuración.", e);
				}
			}
		}
		return instance;
	}
	
	/**
	 * Devuelve el valor de la clave pasada por parámetro
	 * @param key Clave 
	 * @return Vaos del fichero de propiedades
	 */
	public static String getValue(String key) {
		return properties.getProperty(key);
	}
}
