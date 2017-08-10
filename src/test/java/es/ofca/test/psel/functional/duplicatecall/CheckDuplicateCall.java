package es.ofca.test.psel.functional.duplicatecall;

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
import es.ofca.test.psel.common.constant.Constants.Methods;
import es.ofca.test.psel.common.utils.DataProviderUtils;
import es.ofca.test.psel.functional.GeneralForm;
import es.ofca.test.psel.functional.pdf.ValidatePDF;

/**
 * Clase que permite realizar las pruebas funcionales de la aplicación Web hace
 * referencia al CASO de Test: http://xamp.sacyl.es/jira/browse/PSEL-23
 * 
 * @author jluisf
 */
public class CheckDuplicateCall extends GeneralForm {

	private static final Logger LOGGER = Logger.getLogger(CheckDuplicateCall.class.getName());

	/**
	 * Método que crea una solicitud para una convocatoria (ejem: Libre
	 * Ordinario) cuando ya se ha registrado previamente otra solicitud con el
	 * mismo NIF/NIE.
	 * 
	 * @param userData
	 *            DataSource que contiene los diferentes tipos de convocatoria
	 *            Ejem: Libre Ordinario
	 * @throws Exception
	 *             Excepción producida en el método
	 */
	@Test(dataProvider = "datosConvocatoriaOK", groups = { "psel.duplicate.call" })
	public void createLibreOrdDuplicateCall(UserData userData) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Pruebas funcionales de referencia al CASO de Test: http://xamp.sacyl.es/jira/browse/PSEL-23");
			LOGGER.info("WebDriverSelenium.createLibreOrdDuplicateCall");
		}

		// PRECONDICIONES
		// Tiene que existir al menos una convocatoria dentro del periodo de
		// registro de solicitudes
		// con un NIF/NIE que ya tuviera grabada con anterioridad una solicitud
		// para la convocatoria de prueba
		// NIF: 33540149P

		// PASO 1
		// Se establece la conexión
		connet();

		// PASO 2 y PASO3
		// Se establece la convocatoria
		selectConvocatoria(userData);

		// PASO 4
		// Se rellenan todos los campos con valores válidos
		invokeMethods(userData, Methods.CLEAR, AccessType.ORD_FREE, true);
		invokeMethods(userData, Methods.FILL, AccessType.ORD_FREE, true);

		// Se cambia el foco
		focus();
		
		// Se guarda
		save();

		// PASO 5
		// Se comprueba que aparece una ventana de confirmación informando de
		// que ya
		// existe una solicitud de esta convocatoria con el NIF/NIE introducido
		if (validateMessageDuplicateCall()) {
			confirmDuplicateCall(false);
		}

		// PASO 6
		// Hacer algun cambio en el formulario y volver a pulsar GENERAR
		// SOLICITUD PDF
		clearAddress();
		fillAddress();
		
		//Se establece el NIF
		setValueNIF();
		
		// Se cambia el foco
		focus();
		
		// Se guarda
		clickAndSaveFile();
		
		if (validateMessageDuplicateCall()) {
			confirmDuplicateCall(true);
		}

		// PASO 7
		// Se comprueba que se ha generado correctamente el fichero de la
		// solicitud
		String fileName = existPDFfileRequest();
		validateMessagePDFgenerate();

		// PASOS 8 y 9
		//Validar PDF
		String pathFileName = getURLDownload() + File.separator + fileName;
		ValidatePDF.validatePDF(userData, pathFileName, getMapValues());
		
		// Se imprime el formulario
		print();
	}

	/* Parametros de un login OK */
	@DataProvider(name = "datosConvocatoriaOK")
	public Object[][] createDataLoginOK() {
		// Se recogen los usuarios
		UsersData.getInstance();
		Map<String, List<UserData>> mapUsers = UsersData.getMapUsers();

		String[] roles = new String[] { Access.LIBRE_ORD.getValue() };
		Object[][] result = DataProviderUtils.createObjectMultiArray(mapUsers, 1, roles);

		return result;
	}
}