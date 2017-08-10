package es.ofca.test.psel.common.factory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import es.ofca.test.psel.common.constant.Constants;

public abstract class PropertiesFactory {
	
	private static final Logger LOGGER = Logger.getLogger(PropertiesFactory.class);
	protected Reader reader = null;

	protected PropertiesFactory(String dirFile) throws IOException {	
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("PropertiesFactory");
		}
	
		InputStream utf8Stream = null;
		try {
			//Variable del sistema establecida en el pom.xml
			String resourceTestDirectory = System.getProperty(Constants.TEST_RESOURCES_DIRECTORY);
			
			// Si no viene establecida la variable del sistema.
			if (resourceTestDirectory == null) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Variable del Sistema no establecida: " + Constants.TEST_RESOURCES_DIRECTORY);
				}
				utf8Stream = getClass().getResourceAsStream(dirFile);
			} else {
				String absolutePath = resourceTestDirectory + dirFile;
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("FILE instance " + absolutePath);
				}
				utf8Stream = new FileInputStream(absolutePath);
			}
			if (utf8Stream == null) {
				LOGGER.error("Fichero no encontrado:" + dirFile);
				throw new FileNotFoundException();
			}

			reader = new InputStreamReader(utf8Stream, Constants.UTF_8);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
	}
}
