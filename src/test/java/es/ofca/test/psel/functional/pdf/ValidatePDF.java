package es.ofca.test.psel.functional.pdf;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.AssertJUnit;

import es.ofca.test.psel.common.barcode.ReadBarCode;
import es.ofca.test.psel.common.beans.UserData;
import es.ofca.test.psel.common.beans.ValueForm;
import es.ofca.test.psel.common.constant.Constants;
import es.ofca.test.psel.common.pdf.ReadContentPDF;
import es.ofca.test.psel.common.utils.PropertiesFile;
import es.ofca.test.psel.functional.constant.FunctionalConstants;

/**
 * Clase qyue permite validar un PDF
 * 
 * @author dlago
 */
public class ValidatePDF {
	
	private static final Logger LOGGER = Logger.getLogger(ValidatePDF.class);
	
	/**
	 * Método que valida el PDF generado.
	 * @param userData Datos del usuario
	 * @param pathFileName Path del fichero PDF
	 * @param mapValues Mapa de valores
	 * @throws Exception Excepción producida
	 */
	public static void validatePDF(UserData userData, String pathFileName, Map<String, ValueForm> mapValues) throws Exception {
		
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ValidadePDF.validatePDF");
		}
		
		if ((pathFileName != null) && (!pathFileName.isEmpty())) {
						
			// Lectura del PDF
			Map<String,String> valuesMapPDF = ReadContentPDF.printFields(pathFileName);
			
			// Si ambos mapas contienen datos 
			if (((mapValues != null) && (!mapValues.isEmpty())) &&
					((valuesMapPDF != null) && (!valuesMapPDF.isEmpty()))) {
				
				// Se recorren los valores del Formulario
				Iterator<String> keySet = mapValues.keySet().iterator();
				while (keySet.hasNext()) {
					String key = (String) keySet.next();
					ValueForm valueForm = (ValueForm) mapValues.get(key);
					
					String fieldPDF = valueForm.getFieldPDF();
					String value = valueForm.getValue();
					
					if (fieldPDF != null) {
						
						// Si contiene la clave
						if (valuesMapPDF.containsKey(fieldPDF)){
							
							// Se saca el valor del PDF
							String valuePDF = valuesMapPDF.get(fieldPDF);
							
							if (valuePDF != null) {
								switch (valuePDF) {
									case Constants.SI : valuePDF = Boolean.TRUE.toString();
														break;
									case Constants.NO : valuePDF = Boolean.FALSE.toString();
														break;
									default: break;
								}
							}
							
							if ((value == null) && (valuePDF == null)) {
									// Si son iguales se elimina el valor del mapa de objetos PDF
									valuesMapPDF.remove(fieldPDF);
							} else {
								if ((value != null) && (valuePDF != null)) {
									if (value.equals(valuePDF)) {
										// Si son iguales se elimina el valor del mapa de objetos PDF
										valuesMapPDF.remove(fieldPDF);
									} else {
										if (LOGGER.isInfoEnabled()) {
											LOGGER.info("ValidadePDF.validatePDF: Error al validar el PDF. Valor PDF: " + valuePDF + ", Valor Formulario: " + value);
										}
										AssertJUnit.assertFalse(true);
									}
								} else {
									if (valuePDF == null) {
										// Si es igual a null se elimina
										valuesMapPDF.remove(fieldPDF);
									} else {
										if (LOGGER.isInfoEnabled()) {
											LOGGER.info("ValidadePDF.validatePDF: Error al validar el PDF. Valor PDF: " + valuePDF + ", Valor Formulario: " + (value != null ? value : "null"));
										}
										AssertJUnit.assertFalse(true);
									}
								}
							}
						}
					}
				}
				
				// Se miran los campos del PDF que llevan un tratamiento.
				if ((valuesMapPDF != null) && (!valuesMapPDF.isEmpty())) {
					
					//Se chequea el código del Patrón
					checkCodeBar(valuesMapPDF, pathFileName);
					
					// Se chequea el destinatario
					checkReceiver(valuesMapPDF, userData);
					
					//Se chequea el tipo de Convocatoria Seleccionada.
					checkCodeCall(valuesMapPDF, userData);
				}	
			}
		}
	}
	
	
	/**
	 * Comprueba el código de barras con su valor en el PDF
	 * @param valuesMapPDF Valores del PDF
	 * @param pathFileName Nombre del fichero del PDF
	 * @throws Exception Excepción producida
	 */
	private static void checkCodeBar(Map<String,String> valuesMapPDF, String pathFileName) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ValidadePDF.checkCodeBar");
		}

		if (pathFileName != null) {
			// Sacar la imagen del PDF
			String pathImage = null;
			try {
				pathImage = ReadContentPDF.readImage(pathFileName);
			} catch (Exception e1) {
				LOGGER.error("ValidadePDF.checkCodeBar: Error al leer el PDF: " + pathFileName);
				throw e1;
			}
			
			//Leer el código de barras
			String barcode = null;
			try {
				barcode = ReadBarCode.decode(pathImage);
			} catch (Exception e) {
				LOGGER.error("ValidadePDF.checkCodeBar: Error al leer el código de barras: " + pathImage);
			}
			
			if (barcode != null) {
				String value = valuesMapPDF.get(FunctionalConstants.CODIGO_PATRON);
				AssertJUnit.assertEquals(barcode, value);
			}
		}
	}
	
	/**
	 * Comprueba el destinatario de la convocatoria
	 * @param valuesMapPDF Valores del PDF
	 * @param userData Usuario lanzado
	 * @throws Exception Excepción producida
	 */
	private static void checkReceiver(Map<String,String> valuesMapPDF, UserData userData) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ValidadePDF.checkReceiver");
		}
	
		if (userData != null) {
			// Sacar destinatario
			String destinatario = userData.getReceiver();
			
			if (destinatario != null) {
				String value = valuesMapPDF.get(FunctionalConstants.DESTINATARIO_SOLICIT);
				AssertJUnit.assertEquals(destinatario, value);
			}
		}
	}
	
	
	
	/**
	 * Método que valida la categoria seleccionada
	 * @param valuesMapPDF Valores del PDF
	 * @param userData Usuario lanzado
	 * @throws Exception Excepción producida
	 */
	private static void checkCodeCall(Map<String,String> valuesMapPDF, UserData userData) throws Exception {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ValidadePDF.checkCodeCall");
		}
	
		// Se sacan los datos de las convocatorias
		String[] tokens = userData.getUserName().split(" - ");
		String code = "";
		String call = "";
		String accessType = "";
		if ((tokens == null) || (tokens.length != 3)) {
			AssertJUnit.assertFalse(true);
		} else {
			
			// Se comprueba el código de la categoría
			code = tokens[0].toString().trim();
			String codePDF = valuesMapPDF.get(FunctionalConstants.CODIGO_CONVOCATORIA);
			AssertJUnit.assertEquals(code, codePDF);

			// Se comprueba la categoría
			call = tokens[1].toString().trim();
			String callPDF = valuesMapPDF.get(FunctionalConstants.CONVOCATORIA_CATEG);
			AssertJUnit.assertEquals(call, callPDF);
			
			// Se comprueba el tipo de acceso
			accessType = tokens[2].toString().trim();
			
			// Se sacan los datos del tipo de Acceso
			String[] tokensAccess = accessType.split("-");
			
			if ((tokensAccess == null) || (tokensAccess.length != 2)) {
				AssertJUnit.assertFalse(true);
			} else {
			
				// Se comprueba si es de Libre Acceso o Promoción interna
				String access1 = tokensAccess[0].toString().trim();
				if (access1.equals(PropertiesFile.getValue(FunctionalConstants.TYPE_ACCESS_LIBRE))) {
					String valueLibre = valuesMapPDF.get(FunctionalConstants.TIPO_LIBRE);
					AssertJUnit.assertEquals(Constants.SI, valueLibre);
				} else {
					if (access1.equals(PropertiesFile.getValue(FunctionalConstants.TYPE_ACCESS_INTERNA))) {
						String valueInter = valuesMapPDF.get(FunctionalConstants.TIPO_INTER);
						AssertJUnit.assertEquals(Constants.SI, valueInter);
					} else {
						AssertJUnit.assertFalse(true);
					}
				}
				
				// Se comprueba si tiene discapacidad o no
				String access2 = tokensAccess[1].toString().trim();
				String valueDis = valuesMapPDF.get(FunctionalConstants.TIPO_DIS);
				if (access2.equals(PropertiesFile.getValue(FunctionalConstants.TYPE_ACCESS_DISCAPACIDAD))) {
					AssertJUnit.assertEquals(Constants.SI, valueDis);
				} else {
					if (access2.equals(PropertiesFile.getValue(FunctionalConstants.TYPE_ACCESS_ORDINARIO))) {
						AssertJUnit.assertNull(valueDis);
					} else {
						AssertJUnit.assertFalse(true);
					}
				}
			}		
		}
	}
}
