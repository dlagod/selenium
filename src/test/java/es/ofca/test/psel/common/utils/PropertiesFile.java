package es.ofca.test.psel.common.utils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

import es.ofca.test.psel.common.factory.PropertiesFactory;

/**
 * Clase que permite leer un fichero de propiedades
 * 
 * @author dlago
 *
 */
public class PropertiesFile extends PropertiesFactory {
	
	private static final Logger LOGGER = Logger.getLogger(PropertiesFile.class);
	private static final String PROPERTIES_FILE = File.separator + "properties" + File.separator + "pselTest.properties";
	
	private static PropertiesFile instance = null;
	private static Properties properties = null;

	private PropertiesFile(String dirFile) throws IOException {		
		super(dirFile);
		properties = new Properties();
		properties.load(reader);
	}

	/**
	 * Clase que permite instanciar un fichero de propiedades
	 * @return Devuelve la instancia del fichero de propiedades
	 */
	public static synchronized PropertiesFile getInstance() {
		if (instance == null) {
			try {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("PropertiesFile.getInstance");
				}
				instance = new PropertiesFile(PROPERTIES_FILE);
			} catch (IOException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("Se ha producido un error al cargar el fichero de propiedades.", e);
				}
			}
		}
		return instance;
	}
	
	/**
	 * Devuelve el valor de la clave pasada por parámetro
	 * @param key Clave 
	 * @return Valores del fichero de propiedades
	 */
	public static String getValue(String key) {
		return properties.getProperty(key);
	}
	
	
	/**
	 * Devuelve el valor de la clave pasada por parámetro
	 * @param key Clave 
	 * @param parameters Parametros a pasar en el fichero de Propiedades
	 * @return Valores del fichero de propiedades
	 */
	public static String getValueWithParameters(String key, Object... parameters) {
		return MessageFormat.format(
				properties.getProperty(key), parameters);
	}
}
