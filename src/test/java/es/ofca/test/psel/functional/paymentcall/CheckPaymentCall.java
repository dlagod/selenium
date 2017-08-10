package es.ofca.test.psel.functional.paymentcall;

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
 * referencia al CASO de Test: http://xamp.sacyl.es/jira/browse/PSEL-81
 * 
 * @author jluisf
 */
public class CheckPaymentCall extends GeneralForm {

	private static final Logger LOGGER = Logger.getLogger(CheckPaymentCall.class.getName());

	/**
	 * Método que crea una solicitud para una convocatoria (ejem: Libre
	 * Ordinario) y verifica la apertura del enlace para obtener el formulario de pago 046
	 * 
	 * @param userData
	 *            DataSource que contiene los diferentes tipos de convocatoria
	 *            Ejem: Libre Ordinario
	 * @throws Exception
	 *             Excepción producida en el método
	 */
	@Test(dataProvider = "datosConvocatoriaOK", groups = { "psel.payment.call" })
	public void createLibreOrdPaymentCall(UserData userData) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Pruebas funcionales de referencia al CASO de Test: http://xamp.sacyl.es/jira/browse/PSEL-81");
			LOGGER.info("WebDriverSelenium.createLibreOrdPaymentCall");
		}

		// PRECONDICIONES
		// Tiene que existir al menos una convocatoria dentro del periodo de
		// registro de solicitudes
		
		
		// PASO 1
		// Se establece la conexión
		connet();

		// Se establece la convocatoria
		selectConvocatoria(userData);

		// PASO 2
		// Se rellenan todos los campos con valores válidos
		invokeMethods(userData, Methods.CLEAR, AccessType.ORD_FREE, false);
		invokeMethods(userData, Methods.FILL, AccessType.ORD_FREE, false);
		
		//Se establece el NIF
		setValueNIF();

		// PASO 3
		// Se cambia el foco
		focus();
		
		// Se guarda
		clickAndSaveFile();

		// Se comprueba que se ha generado correctamente el fichero de la
		// solicitud
		String fileName = existPDFfileRequest();
		validateMessagePDFgenerate();
		
		//Validar PDF
		String pathFileName = getURLDownload() + File.separator + fileName;
		ValidatePDF.validatePDF(userData, pathFileName, getMapValues());

		// Se imprime el formulario
		print();
		
		// PASO 4
		// Acceder al link "Acceso al modelo 046"
		validateAccessPaymentModel046();
	}

	/* Parametros de un login OK */
	@DataProvider(name = "datosConvocatoriaOK")
	public Object[][] createDataLoginOK() {
		// Se recogen los usuarios
		UsersData.getInstance();
		Map<String, List<UserData>> mapUsers = UsersData.getMapUsers();

		String[] roles = new String[] { Access.LIBRE_ORD.getValue() };
		Object[][] result = DataProviderUtils.createObjectMultiArray(mapUsers, -1, roles);

		return result;
	}
}