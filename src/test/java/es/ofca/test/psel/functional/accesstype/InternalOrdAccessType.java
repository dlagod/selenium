package es.ofca.test.psel.functional.accesstype;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import es.ofca.test.psel.common.annotations.Settings.AccessType;
import es.ofca.test.psel.common.beans.UserData;
import es.ofca.test.psel.common.beans.UsersData;
import es.ofca.test.psel.common.beans.UsersData.Access;
import es.ofca.test.psel.common.constant.Constants;
import es.ofca.test.psel.common.constant.Constants.Methods;
import es.ofca.test.psel.common.utils.DataProviderUtils;
import es.ofca.test.psel.functional.GeneralForm;
import es.ofca.test.psel.functional.pdf.ValidatePDF;

/**
 * Clase que permite realizar las pruebas funcionales de la aplicación Web hace
 * referencia al CASO de Test: http://xamp.sacyl.es/jira/browse/PSEL-19
 * 
 * @author jluisf
 */
public class InternalOrdAccessType extends GeneralForm {

	private static final Logger LOGGER = Logger.getLogger(InternalOrdAccessType.class.getName());

	/**
	 * Método que crea una solicitud para una convocatoria Interna Ordinaria.
	 * 
	 * @param userData
	 *            DataSource que contiene los diferentes tipos de convocatoria
	 *            Interna Ordinaria
	 * @throws Exception
	 *             Excepción producida en el método
	 */
	@Test(dataProvider = "datosConvocatoriaOK", groups = { "grupo.internal.ord.ok" })
	public void createInternalOrdAccessType(UserData userData) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Pruebas funcionales de referencia al CASO de Test: http://xamp.sacyl.es/jira/browse/PSEL-19");
			LOGGER.info("WebDriverSelenium.createInternalOrdAccessType");
		}

		// PASO 1
		// Se establece la conexión
		connet();

		// PASO 2 y PASO3
		// Se establece la convocatoria
		selectConvocatoria(userData);

		// PASO 4
		// Se chequean las secciones de la página
		checkFrames();

		// Se chequean los datos de las convocatorias
		checkCodeCall(userData);

		// PASO 5
		// Se limpian los valores
		invokeMethods(userData, Constants.Methods.CLEAR, AccessType.ORD_INTERNAL, false);

		// Se guarda
		save();

		// Se validan que salgan todos los campos obligatorios
		requiredValuesGeneralAccessType(userData, AccessType.ORD_INTERNAL);

		// PASO 6
		// Se chequean los campos Requeridos
		invokeMethods(userData, Methods.FILL_REQUIRED, AccessType.ORD_INTERNAL, false);

		// Se chequean los carácteres no válidos de los campos
		invokeMethods(userData, Methods.CHECK_VALUE, AccessType.ORD_INTERNAL, false);

		// PASO 7
		// Se rellenando todos los campos con valores válidos
		invokeMethods(userData, Methods.CLEAR, AccessType.ORD_INTERNAL, false);
		invokeMethods(userData, Methods.FILL, AccessType.ORD_INTERNAL, false);
		
		//Se establece el NIF
		setValueNIF();

		// Se cambia el foco
		focus();
		
		// Se guarda
		clickAndSaveFile();

		// PASO 8
		// Se comprueba que se ha generado correctamente el fichero de la
		// solicitud
		String fileName = existPDFfileRequest();
		validateMessagePDFgenerate();

		// PASO 9
		// Validación de PDF Generado:
		// Comprobar que en los campos del PDF se ha volcado el dato que se
		// metió en los correspondientes campos del formulario
		// + Código de barras, destinatario
		String pathFileName = getURLDownload() + File.separator + fileName;
		ValidatePDF.validatePDF(userData, pathFileName, getMapValues());

		// PASO 10
		// Se imprime el formulario
		print();

	}

	
	/* Parametros de un login OK */
	@DataProvider(name = "datosConvocatoriaOK")
	public Object[][] createDataLoginOK() {
		// Se recogen los usuarios
		UsersData.getInstance();
		Map<String, List<UserData>> mapUsers = UsersData.getMapUsers();

		String[] roles = new String[] { Access.PROM_INTER_ORD.getValue() };
		Object[][] result = DataProviderUtils.createObjectMultiArray(mapUsers, -1, roles);

		return result;
	}
}