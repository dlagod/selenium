package es.ofca.test.psel.functional;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.AssertJUnit;

import es.ofca.test.psel.common.annotations.Settings;
import es.ofca.test.psel.common.annotations.Settings.AccessType;
import es.ofca.test.psel.common.beans.UserData;
import es.ofca.test.psel.common.beans.UsersData.Access;
import es.ofca.test.psel.common.beans.ValueForm;
import es.ofca.test.psel.common.constant.Constants;
import es.ofca.test.psel.common.constant.Constants.Methods;
import es.ofca.test.psel.common.selenium.GridInfoExtractor;
import es.ofca.test.psel.common.selenium.WebDriverSelenium;
import es.ofca.test.psel.common.utils.ConfigFile;
import es.ofca.test.psel.common.utils.DateUtils;
import es.ofca.test.psel.common.utils.GeneratorRamdomDNI;
import es.ofca.test.psel.common.utils.GeneratorRandomNIE;
import es.ofca.test.psel.common.utils.PropertiesFile;
import es.ofca.test.psel.common.utils.URLProcess;
import es.ofca.test.psel.functional.constant.FunctionalConstants;

/**
 * Clase que contiene los métodos principales del formulario General de PSEL
 * 
 * @author dlago
 */
public class GeneralForm extends WebDriverSelenium {

	private static final Logger LOGGER = Logger.getLogger(GeneralForm.class.getName());

	// Atributos comunes a la Clase
	private String dni = "";
	private String nie = "";
	private Map<String, ValueForm> mapValues = new HashMap<String, ValueForm>();

	private AccessType accessTypeCall;
	private boolean requireDuplicateCall = false;

	/**
	 * Método que devuelve un DNI
	 * 
	 * @return devuelve un DNI
	 */
	public String getDni() {
		return dni;
	}
	
	
	/**
	 * Método que establece el NIF
	 * @throws Exception producida
	 */
	public void setValueNIF() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.setValueNIF");
		}
		
		// Locator NIF
		By locator = By.id("generalform:accordionPanel:nifNie");

		// Se establece el NIF
		setDni(untilGetAttributeElement(locator, Constants.VALUE));
	}
	

	/**
	 * Método que establece un DNI
	 * 
	 * @param dni
	 *            DNI
	 */
	public void setDni(String dni) {
		this.dni = dni;
	}

	/**
	 * Método que devuelve un NIE
	 * 
	 * @return devuelve un NIE
	 */
	public String getNie() {
		return nie;
	}

	/**
	 * Método que establece un NIE
	 * 
	 * @param nie
	 *            NIE
	 */
	public void setNie(String nie) {
		this.nie = nie;
	}

	/**
	 * Devuelve el mapa de campos
	 * 
	 * @return Mapa de campos
	 */
	public Map<String, ValueForm> getMapValues() {
		return mapValues;
	}

	/**
	 * Establece el mapa de campos
	 * 
	 * @param mapValues
	 *            Mapa de campos
	 */
	public void setMapValues(Map<String, ValueForm> mapValues) {
		this.mapValues = mapValues;
	}

	/**
	 * Método que establece el Driver de Selenium
	 * 
	 * @param driver
	 *            Driver de Selenium
	 */
	public void loadDriver(RemoteWebDriver driver) {
		setWebDriver(driver);
	}

	/**
	 * Método que añade un elemento al mapa de campos
	 * 
	 * @param name
	 *            Nombre del campo
	 * @param value
	 *            Valor del campo
	 * @param propertySummary
	 *            Valor del fichero de propiedades para el nombre del campo.
	 * @param propertyDetail
	 *            Valor del fichero de propiedades para el campo detalle.
	 * @param fieldPDF 
	 * 			  Mapeo del campo con el PDF
	 * @param required
	 *            Parámetro booleano para indicar si es requerido
	 * @throws Devuelve
	 *             el objeto creado
	 */
	public ValueForm addMapValues(String name, String value, String propertySummary, String propertyDetail,
			String fieldPDF, boolean required) {
		ValueForm valueForm = new ValueForm(name, value, propertySummary, propertyDetail, fieldPDF, required);
		mapValues.put(name, valueForm);
		return valueForm;
	}

	/**
	 * Método que conecta con la aplicación de PSEL
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void connet() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.connet- PASO: 1");
		}
		String urlPath = "inicio.xhtml";
		getDriver().get(ConfigFile.getValue(Constants.BASE_URL) + urlPath);

		// Se comprueba que se ha seleccionado la opción correcta
		AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.MESSAGE_WELCOME),
				getDriver().findElement(By.cssSelector("p")).getText());
	}

	/**
	 * Método que establece la convocatoria
	 * 
	 * @param userData
	 *            Datasource con las diferentes convocatorias
	 * @throws Exception
	 *             Excepción producida
	 */
	public void selectConvocatoria(UserData userData) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.setConvocatoria - PASO: 2: " + userData.getUserName());
		}
		
		By locator = By.xpath("//*[local-name()='div'][contains(@id,':convocatorias') and (not(contains(@id,':convocatorias_')))]");
		WebElement panel = untilFindElement(locator);
		if (!panel.isSelected()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("GeneralForm.selectConvocatoria - panel.isDisplayed()");
			}
			untilClickElement(locator);
		}

		// Se establece la el valor de la provincia		
		untilSelectElement(By.xpath("//*[local-name()='ul'][contains(@id,':convocatorias_items')]/*[local-name()='li']"), userData.getUserName());
		
		
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.setConvocatoria - PASO: 3");
		}
		
		// Se pulsa en el botón acceder formulario
		untilClickElement(By.xpath("//*[local-name()='button'][contains(@id,':acceder')]"));
	}

	/**
	 * Método que establece el AccessType de la invocación
	 * 
	 * @param accessTypeCall
	 *            AccessType de la invocación
	 */
	public void loadAccessTypeCall(AccessType accessTypeCall) {
		this.accessTypeCall = accessTypeCall;
	}

	/**
	 * Método que establece la condición de solicitud duplicada
	 * 
	 * @param requireDuplicateCall
	 */
	public void loadRequireDuplicateCall(boolean requireDuplicateCall) {
		this.requireDuplicateCall = requireDuplicateCall;
	}

	/**************************************************************************
	 * INICIO LIMPIAR METODOS
	 **************************************************************************/

	/**
	 * Método que limpia el NIF
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 1)
	public void clearNIF() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearNIF");
		}

		// Se limpia el NIF
		By locator = By.id("generalform:accordionPanel:nifNie");
		untilClearElement(locator);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NIF), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NIF), FunctionalConstants.ERROR_MESSAGE_NIF, FunctionalConstants.SOLICITANTE_NIF, true);

		// Se comprueba que se ha modificado el DNI
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, untilGetAttributeElement(locator, Constants.VALUE));
	}

	/**
	 * Método que limpia los apellidos.
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 2)
	public void clearSurname() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearSurname");
		}

		// Se establece los Apellidos
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:apellidos"));
		webElement.clear();

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_SURNAME), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_SURNAME), FunctionalConstants.ERROR_MESSAGE_SURNAME,
				FunctionalConstants.SOLICITANTE_APELLIDOS, true);

		// Se comprueba que se han modificado los apellidos
		AssertJUnit.assertEquals(Constants.CADENA_VACIA,
				webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Método que limpia el Nombre
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 3)
	public void clearName() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearName");
		}

		// Se limpia el nombre
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:nombre"));
		webElement.clear();

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NAME), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME), FunctionalConstants.ERROR_MESSAGE_NAME,
				FunctionalConstants.SOLICITANTE_NOM, true);

		// Se comprueba que se han modificado el nombre
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Método que limpia la Fecha de Nacimiento
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 4)
	public void clearBirthDate() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearBirthDate");
		}

		// Se limpia la Fecha de Nacimiento
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:fechaNacimiento_input"));
		webElement.clear();

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BIRTHDATE), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_BIRTHDATE),
				FunctionalConstants.ERROR_MESSAGE_BIRTHDATE, FunctionalConstants.SOLICITANTE_FECHN, true);

		// Se comprueba que se han modificado la Fecha de Nacimiento
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Método que limpia la nacionalidad
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 5)
	public void clearNacionality() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearNacionality");
		}

		// Se limpia la Nacionalidad
		By locator = By.id("generalform:accordionPanel:nacionalidad");
		untilClearElement(locator);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NACIONALITY), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NACIONALITY),
				FunctionalConstants.ERROR_MESSAGE_NACIONALITY, FunctionalConstants.SOLICITANTE_NAC, true);

		// Se comprueba que se han modificado la Nacionalidad
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, untilGetAttributeElement(locator, Constants.VALUE));
	}

	/**
	 * Método que limpia la dirección
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 6)
	public void clearAddress() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearAddress");
		}

		// Se limpia la Dirección
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:direccion"));
		webElement.clear();

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_ADDRESS), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_ADDRESS), FunctionalConstants.ERROR_MESSAGE_ADDRESS,
				FunctionalConstants.SOLICITANTE_DIR, true);

		// Se comprueba que se han modificado la Dirección
		AssertJUnit.assertEquals(Constants.CADENA_VACIA,
				webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Método que limpia el municipio
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 7)
	public void clearCity() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearCity");
		}

		// Se limpia el Municipio
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:municipio"));
		webElement.clear();

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CITY), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CITY), FunctionalConstants.ERROR_MESSAGE_CITY,
				FunctionalConstants.SOLICITANTE_MUN, true);

		// Se comprueba que se han modificado el Municipio
		AssertJUnit.assertEquals(Constants.CADENA_VACIA,
				webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Limpia la provincia
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 8)
	public void clearProvice() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearProvice");
		}

		// Se establece la Provincia
		By locator = By.id("generalform:accordionPanel:provincias_label");
		untilClickElement(locator);
		
		//Comprobar que esta display el acordion
		WebElement panel = untilFindElement(By.id("generalform:accordionPanel:provincias_panel"));
		if (!panel.isDisplayed()) {
			untilClickElement(locator);
		}

		untilSelectElement(By.xpath("//*[local-name()='ul'][@id='generalform:accordionPanel:provincias_items']/*[local-name()='li']"),
				PropertiesFile.getValue(FunctionalConstants.MESSAGE_PROVINCE));

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_PROVINCE),
				PropertiesFile.getValue(FunctionalConstants.MESSAGE_PROVINCE),
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_PROVINCE),
				FunctionalConstants.ERROR_MESSAGE_PROVINCE, FunctionalConstants.SOLICITANTE_PROV, true);

		// Se comprueba que se han modificado la Provincia
		AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.MESSAGE_PROVINCE),
				untilGetTextElement(locator, PropertiesFile.getValue(FunctionalConstants.MESSAGE_PROVINCE)));
	}

	/**
	 * Limpia el código postal
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 9)
	public void clearPostalCode() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearPostalCode");
		}

		// Se limpia el Código Postal
		By locator = By.id("generalform:accordionPanel:cp");		
		untilClearElement(locator);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_POSTALCODE), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_POSTALCODE),
				FunctionalConstants.ERROR_MESSAGE_POSTAL_CODE, FunctionalConstants.SOLICITANTE_CP, true);
		
		//Se cambia el foco
		focus();
		
		//Se espera a la página
		waitUntilDocumentIsReady();

		// Se comprueba que se han modificado el Código Postal
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, untilGetAttributeElement(locator, Constants.VALUE, Constants.CADENA_VACIA));
	}

	/**
	 * Limpia el País
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 10)
	public void clearCountry() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearCountry");
		}

		// Se limpia el País
		By locator = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY) + "')]]/following-sibling::td/div/input");
		untilClearElement(locator);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_COUNTRY), FunctionalConstants.ERROR_MESSAGE_COUNTRY,
				null, true);

		// Se comprueba que se han modificado el País
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, untilGetAttributeElement(locator, Constants.VALUE, Constants.CADENA_VACIA));
	}

	/**
	 * Limpia la titulación
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 11)
	public void clearDegree() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearDegree");
		}

		// Se limpia la Titulación
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:titulacion"));
		webElement.clear();

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DEGREE), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_DEGREE), FunctionalConstants.ERROR_MESSAGE_DEGREE,
				FunctionalConstants.SOLICITANTE_TITUL, true);
		
		// Se comprueba que se han modificado la Titulación
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Limpia la Provincia de Examen
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 12)
	public void clearExamProvice() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearExamProvice");
		}

		// Se establece el primer elemento del select ya que a veces no existen
		// las mismas provincias
		String provinciaSelect = "";

		WebElement webElement = untilFindElement(By.id("generalform:accordionPanel:provinciaSelect_input"));
		webElement.isEnabled();
		Select select = new Select(webElement);
		List<WebElement> options = select.getOptions();
		if ((options == null) || (options.isEmpty())) {
			AssertJUnit.assertFalse(true);
		} else {
			if (options.size() > 1) {
				provinciaSelect = PropertiesFile.getValue(FunctionalConstants.MESSAGE_EXAM_PROVINCE);
				
				// Se establece la Provincia de Examen
				untilClickElement(By.id("generalform:accordionPanel:provinciaSelect_label"));
		
				untilClickElement(By.xpath(
						"//li[contains(@id,':provinciaSelect_') and contains(@data-label,'" + provinciaSelect + "')]"));

				// Add campo a lista
				addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_EXAM_PROVINCE),
						PropertiesFile.getValue(FunctionalConstants.MESSAGE_EXAM_PROVINCE),
						PropertiesFile.getValue(FunctionalConstants.SUMMARY_EXAM_PROVINCE),
						FunctionalConstants.ERROR_MESSAGE_EXAM_PROVINCE, FunctionalConstants.PROVINCIA_EXAMEN, false);

				// Se comprueba que se han modificado la Provincia de Examen
				AssertJUnit.assertEquals(provinciaSelect, untilGetTextElement(By.id("generalform:accordionPanel:provinciaSelect_label")));
			} else {
				// Add campo a lista
				addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_EXAM_PROVINCE),
						options.get(0).getAttribute(Constants.VALUE),
						PropertiesFile.getValue(FunctionalConstants.SUMMARY_EXAM_PROVINCE),
						FunctionalConstants.ERROR_MESSAGE_EXAM_PROVINCE, FunctionalConstants.PROVINCIA_EXAMEN, true);
			}
		}
	}

	/**
	 * Limpia el correo electrónico
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 13)
	public void clearEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearEmail");
		}

		// Se limpia el correo electrónico
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:email"));
		webElement.clear();

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_EMAIL), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_EMAIL),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, false);

		// Se comprueba que se han modificado el correo electrónico
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Limpia el correo electrónico
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 14)
	public void clearConfirmEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearConfirmEmail");
		}

		if (isActiveConfirmEmail()) {

			// Se limpia el correo electrónico de confirmación
			WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:email2"));
			webElement.clear();

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CONFIRM_EMAIL), Constants.CADENA_VACIA,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_CONFIRM_EMAIL),
					FunctionalConstants.ERROR_MESSAGE_FORMAT_CONFIRM_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, false);

			// Se comprueba que se han modificado el email de confirmación
			AssertJUnit.assertEquals(Constants.CADENA_VACIA, webElement.getAttribute(Constants.VALUE));
		}
	}

	/**
	 * Método que verifica si está activo el campo del correo electrónico
	 * 
	 * @return Devuelve un boolean si está activo el campo de correo electrónico
	 * @throws Exception
	 */
	public boolean isActiveConfirmEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveConfirmEmail");
		}
		boolean active = false;

		if (!(getDriver().findElement(By.id("generalform:accordionPanel:email")).getAttribute(Constants.VALUE)).isEmpty()) {
			active = true;
		}

		return active;
	}

	/**
	 * Limpia el nombre de la provincia
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 15)
	public void clearNameProvince() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearNameProvince");
		}

		if (isActiveNameProvince()) {

			// Se limpia el correo electrónico de confirmación
			By locator = By.id("generalform:accordionPanel:otraProvincia");
			untilClearElement(locator);

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NAME_PROVINCE), Constants.CADENA_VACIA,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME_PROVINCE),
					FunctionalConstants.ERROR_MESSAGE_NAME_PROVINCE, FunctionalConstants.SOLICITANTE_PROV, false);

			// Se comprueba que se han modificado el nombre de la provincia
			AssertJUnit.assertEquals(Constants.CADENA_VACIA, untilGetAttributeElement(locator, Constants.VALUE));
		}
	}

	/**
	 * Método que verifica si está activo el campo de Nombre de provincia
	 * 
	 * @return Devuelve un boolean si está activo el campo de Nombre de
	 *         provincia
	 * @throws Exception
	 */
	public boolean isActiveNameProvince() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveNameProvince");
		}
		boolean active = false;
		
		// Se comprueba que la provincia tiene el valor seleccionado "Otra"
		if ((PropertiesFile.getValue(FunctionalConstants.MESSAGE_OTHER_PROVINCE))
				.equalsIgnoreCase(untilGetTextElement(By.id("generalform:accordionPanel:provincias_label")))) {
			active = true;
		}

		return active;
	}

	/**
	 * Limpia el grado de discapacidad
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 16)
	public void clearDisability() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearDisability");
		}

		// Se limpia el porcentaje de discapacidad
		WebElement webElement = untilFindElement(By.id("generalform:accordionPanel:number"));
		webElement.clear();

		boolean required = false;
		if (accessTypeCall.equals(AccessType.DISABILITY_FREE)
				|| accessTypeCall.equals(AccessType.DISABILITY_INTERNAL)) {
			required = true;
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DISABILITY), Constants.CADENA_VACIA,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_DISABILITY),
				FunctionalConstants.ERROR_MESSAGE_DISABILITY_PERCENT, null, required);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(Constants.CADENA_VACIA, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Desactiva el check "Exención por discapacidad igual o mayor al 33%"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 17)
	public void clearDisabilityExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearDisabilityExemption");
		}

		// Si está activo se desactiva
		if (isActiveDisabilityExemption()) {
			// Se chequea en el campo label
			getDriver().findElement(By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DISABILITY_EXEMPTION) + "')]]")).click();
		}

	
		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DISABILITY_EXEMPTION), Constants.FALSE,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_BASE_CENTER),
				FunctionalConstants.ERROR_MESSAGE_BASE_CENTER, FunctionalConstants.REDUCCION, false);

		// Se comprueba que se han modificado el check "Exención por
		// discapacidad igual o mayor al 33%"
		AssertJUnit.assertEquals(false, isActiveDisabilityExemption());
	}

	/**
	 * Método que verifica el check
	 * "Exención por discapacidad igual o mayor al 33%" si está activo
	 * 
	 * @return Devuelve un boolean si está activo el campo de Nombre de
	 *         provincia
	 * @throws Exception
	 */
	public boolean isActiveDisabilityExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveDisabilityExemption");
		}

		// Se mira si está activo o no
		String checked = untilGetAttributeElement(By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DISABILITY_EXEMPTION)
				+ "')]]/preceding-sibling::td/div/div/input"), "aria-checked");
		
		return Boolean.parseBoolean(checked);
	}

	/**
	 * Limpia el Centro Base
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 18)
	public void clearBaseCenter() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearBaseCenter");
		}

		// Si está activo se limpia el centro
		if (isActiveDisabilityExemption()) {

			// Se limpia el centro base
			WebElement webElement = getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_BASE_CENTER)
					+ "')]]/following-sibling::td/input"));
			webElement.clear();

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BASE_CENTER), Constants.CADENA_VACIA,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_BASE_CENTER),
					FunctionalConstants.ERROR_MESSAGE_BASE_CENTER, FunctionalConstants.CENTRO_BASE, false);

			// Se comprueba que se han modificado el Centro Base
			AssertJUnit.assertEquals(Constants.CADENA_VACIA, webElement.getAttribute(Constants.VALUE));
		}
	}

	/**
	 * Desactiva el check "Exención por familia numerosa"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 19)
	public void clearFamilyExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearFamilyExemption");
		}

		// Si está activo se desactiva
		if (isActiveFamilyExemption()) {
			// Se chequea en el campo label
			By locatorClick = By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_FAMILY_EXEMPTION) + "')]]");
			By locatorCheck = By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_FAMILY_EXEMPTION)
					+ "')]]/preceding-sibling::td/div/div/input");
			
			untilClickCheck(locatorClick, locatorCheck);
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_FAMILY_EXEMPTION), Constants.FALSE,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NUM_FAMILY_TITLE),
				FunctionalConstants.ERROR_MESSAGE_NUM_FAMILY_TITLE, FunctionalConstants.FAMILIA_NUM, false);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se han modificado el check "Exención por familia
		// numerosa"
		AssertJUnit.assertEquals(false, isActiveFamilyExemption());
	}

	/**
	 * Método que verifica el check "Exención por familia numerosa" si está
	 * activo
	 * 
	 * @return Devuelve un boolean si está activo el campo de Nombre de
	 *         provincia
	 * @throws Exception
	 */
	public boolean isActiveFamilyExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveFamilyExemption");
		}

		// Se mira si está activo o no
		String checked = untilGetAttributeElement(By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_FAMILY_EXEMPTION)
				+ "')]]/preceding-sibling::td/div/div/input"), "aria-checked");

		return Boolean.parseBoolean(checked);
	}

	/**
	 * Limpia el Nº título de familia numerosa
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 20)
	public void clearNumFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearNumFamilyTitle");
		}

		// Si está activo se limpia el Nº título de familia numerosa
		if (isActiveFamilyExemption()) {
		
			// Se limpia el centro base			
			By locator = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_NUM_FAMILY_TITLE)
					+ "')]]/following-sibling::td/input");
			untilClearElement(locator);

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NUM_FAMILY_TITLE), Constants.CADENA_VACIA,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_NUM_FAMILY_TITLE),
					FunctionalConstants.ERROR_MESSAGE_NUM_FAMILY_TITLE, FunctionalConstants.FAMILIA_NUM, false);

			// Se comprueba que se han modificado el Nº título de familia
			// numerosa
			AssertJUnit.assertEquals(Constants.CADENA_VACIA, untilGetAttributeElement(locator, Constants.VALUE));
		}
	}

	/**
	 * Limpia la fecha de caducidad del título
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 21)
	public void clearDateFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clearDateFamilyTitle");
		}

		// Si está activo se limpia la fecha de caducidad del título
		if (isActiveFamilyExemption()) {

			// Se limpia el centro base			
			By locator = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DATE_FAMILY_TITLE)
					+ "')]]/following-sibling::td/span/input");
			untilClearElement(locator);

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DATE_FAMILY_TITLE), Constants.CADENA_VACIA,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_DATE_FAMILY_TITLE),
					FunctionalConstants.ERROR_MESSAGE_DATE_FAMILY_TITLE, FunctionalConstants.FECHA_CADUC, false);

			// Se comprueba que se han modificado la fecha de caducidad del
			// título
			AssertJUnit.assertEquals(Constants.CADENA_VACIA, untilGetAttributeElement(locator, Constants.VALUE));
		}
	}

	/**
	 * Método que verifica el check
	 * "Los datos relativos a la identidad. - ver apartado 3.4.1. a)" si está
	 * activo
	 * 
	 * @return Devuelve un boolean si está activo el campo de Los Datos
	 *         relativos a la Identidad
	 * @throws Exception
	 */
	public boolean isActiveAuthIdentityDataExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveAuthIdentityDataExemption");
		}

		// Se mira si está activo o no
		String checked = untilGetAttributeElement(By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_IDENTITY_DATA_TITLE)
				+ "')]]/preceding-sibling::td/div/div/input"), "aria-checked");

		return Boolean.parseBoolean(checked);
	}

	/**
	 * Método que verifica el check
	 * "Certificación de no haber sido condenado por sentencia firme por algún delito contra la libertad e indemnidad sexual. (ver apartado 9.2.e)"
	 * si está activo
	 * 
	 * @return Devuelve un boolean si está activo el campo de Certificación de
	 *         no haber sido condenado ...
	 * @throws Exception
	 */
	public boolean isActiveAuthCertifCondemnedExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveAuthCertifCondemnedExemption");
		}

		// Se mira si está activo o no
		String checked = untilGetAttributeElement(By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_CERTIF_CONDEMNED_TITLE)
				+ "')]]/preceding-sibling::td/div/div/input"), "aria-checked");

		return Boolean.parseBoolean(checked);
	}

	/**
	 * Método que verifica el check
	 * "Los datos relativos a la discapacidad reconocida en Castilla y León. (ver apartado 3.4.2.c)"
	 * si está activo
	 * 
	 * @return Devuelve un boolean si está activo el campo de autorización de
	 *         los datos relativos a la discapacidad ...
	 * @throws Exception
	 */
	public boolean isActiveAuthDisabilityExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveAuthDisabilityExemption");
		}

		// Se mira si está activo o no
		String checked = untilGetAttributeElement(By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_DISABILITY_TITLE)
				+ "')]]/preceding-sibling::td/div/div/input"), "aria-checked");

		return Boolean.parseBoolean(checked);
	}

	/**
	 * Método que verifica el check
	 * "Los datos relativos a la condición de familia numerosa reconocida en Castilla y León. (ver apartado 3.4.2.d)"
	 * si está activo
	 * 
	 * @return Devuelve un boolean si está activo el campo de autorización de
	 *         los datos relativos a la condición de familia numerosa ...
	 * @throws Exception
	 */
	public boolean isActiveAuthLargeFamilyExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isActiveAuthLargeFamilyExemption");
		}

		// Se mira si está activo o no
		String checked = untilGetAttributeElement(By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_LARGE_FAMILY_TITLE)
				+ "')]]/preceding-sibling::td/div/div/input"), "aria-checked");

		return Boolean.parseBoolean(checked);
	}

	/**
	 * Limpia la Categoría de procedencia
	 * 
	 * @throws Exception
	 *             Excepción producida access = Settings.AccessType.ORD_INTERNAL
	 *             access = Settings.AccessType.DISABILITY_INTERNAL
	 */
	@Settings(order = 22)
	public void clearCategoryOrigin() throws Exception {

		if (accessTypeCall.equals(AccessType.ORD_INTERNAL) || accessTypeCall.equals(AccessType.DISABILITY_INTERNAL)) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("WebDriverSelenium.clearCategoryOrigin");
			}
			
			By locator = By.id("generalform:accordionPanel:categoriaDeProcedencia_label");
			WebElement panel = untilFindElement(locator);
			if (!panel.isSelected()) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("WebDriverSelenium.clearCategoryOrigin - panel.isDisplayed()");
				}
				untilClickElement(locator);
			}
			
			String value = PropertiesFile.getValue(FunctionalConstants.MESSAGE_CATEGORY_ORIGIN);
					
			// Se establece la el valor de la provincia		
			untilSelectElement(By.xpath("//*[local-name()='ul'][@id='generalform:accordionPanel:categoriaDeProcedencia_items']/*[local-name()='li']"), value);

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.MESSAGE_CATEGORY_ORIGIN),
					PropertiesFile.getValue(FunctionalConstants.MESSAGE_CATEGORY_ORIGIN),
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_CATEGORY_ORIGIN_TITLE),
					FunctionalConstants.ERROR_MESSAGE_CATEGORY_ORIGIN_TITLE, FunctionalConstants.SOLICITANTE_CATEG, true);

			AssertJUnit.assertEquals(value, untilGetTextElement(locator));
		}

	}

	/**************************************************************************
	 * FIN LIMPIAR METODOS
	 **************************************************************************/

	/**************************************************************************
	 * INICIO CHECK EMPTY METODOS
	 **************************************************************************/

	/**
	 * Método que chequea el espacio en Blanco en el campo NIF
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 1)
	public ValueForm checkValueBlankSpaceNIF() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceNIF");
		}

		// Asegurarse en borrar el campo
		clearNIF();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el NIF
		By locator = By.id("generalform:accordionPanel:nifNie");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NIF), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NIF), FunctionalConstants.ERROR_MESSAGE_FORMAT_NIF,
				FunctionalConstants.SOLICITANTE_NIF, true);

		// Se comprueba que se ha modificado el DNI
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Apellido
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 2)
	public ValueForm checkValueBlankSpaceSurname() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceSurname");
		}

		// Asegurarse en borrar el campo
		clearSurname();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el Apellido
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:apellidos"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_SURNAME), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_SURNAME),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_SURNAME, FunctionalConstants.SOLICITANTE_APELLIDOS, true);

		// Se comprueba que se ha modificado los Apellidos
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Nombre
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 3)
	public ValueForm checkValueBlankSpaceName() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceName");
		}

		// Asegurarse en borrar el campo
		clearName();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el Nombre
		untilSendKeysElement(By.id("generalform:accordionPanel:nombre"), value);
	
		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NAME), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_NAME, FunctionalConstants.SOLICITANTE_NOM, true);

		// Se comprueba que se ha modificado el nombre
		AssertJUnit.assertEquals(value, untilGetAttributeElement(By.id("generalform:accordionPanel:nombre"),
				Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Nacionalidad
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 4)
	public ValueForm checkValueBlankSpaceNacionality() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceNacionality");
		}

		// Asegurarse en borrar el campo
		clearNacionality();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el valor
		By locator = By.id("generalform:accordionPanel:nacionalidad");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NACIONALITY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NACIONALITY),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_NACIONALITY, FunctionalConstants.SOLICITANTE_NAC, true);

		// Se comprueba que se ha modificado el campo.
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Direccción
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 5)
	public ValueForm checkValueBlankSpaceAddress() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceAddress");
		}

		// Asegurarse en borrar el campo
		clearAddress();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el valor
		By locator = By.id("generalform:accordionPanel:direccion");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_ADDRESS), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_ADDRESS),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_ADDRESS, FunctionalConstants.SOLICITANTE_DIR, true);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Municipio
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 6)
	public ValueForm checkValueBlankSpaceCity() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceAddress");
		}

		// Asegurarse en borrar el campo
		clearCity();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el valor
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:municipio"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CITY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CITY),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_CITY, FunctionalConstants.SOLICITANTE_MUN, true);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Código Postal
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 7)
	public ValueForm checkValueBlankSpacePostalCode() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpacePostalCode");
		}

		// Asegurarse en borrar el campo
		clearPostalCode();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el valor
		untilSendKeysElement(By.id("generalform:accordionPanel:cp"), value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_POSTALCODE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_POSTALCODE),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_POSTAL_CODE, FunctionalConstants.SOLICITANTE_CP, true);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, untilGetAttributeElement(By.id("generalform:accordionPanel:cp"),
				Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo País
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 8)
	public ValueForm checkValueBlankSpaceCountry() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceCountry");
		}
		// Asegurarse en borrar el campo
		clearCountry();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el País
		WebElement webElement = getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY) + "')]]/following-sibling::td/div/input"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_COUNTRY),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_COUNTRY, null, true);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Titulación
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 9)
	public ValueForm checkValueBlankSpaceDegree() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankSpaceDegree");
		}
		// Asegurarse en borrar el campo
		clearDegree();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece la Titulación
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:titulacion"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DEGREE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_DEGREE),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_DEGREE, FunctionalConstants.SOLICITANTE_TITUL, true);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Nombre Provincia
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 10)
	public ValueForm checkValueBlankNameProvince() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankNameProvince");
		}
		// Asegurarse en borrar el campo
		fillOtherProvice();
		clearNameProvince();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el valor
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:otraProvincia"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NAME_PROVINCE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME_PROVINCE),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_NAME_PROVINCE, FunctionalConstants.SOLICITANTE_PROV, false);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Centro Base
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 11)
	public ValueForm checkValueBlankBaseCenter() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankBaseCenter");
		}
		// Asegurarse de borrar el campo
		fillDisabilityExemption();
		clearBaseCenter();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;

		// Se establece el Centro Base
		WebElement webElement = getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_BASE_CENTER)
				+ "')]]/following-sibling::td/input"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BASE_CENTER), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_BASE_CENTER),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_BASE_CENTER, FunctionalConstants.CENTRO_BASE, false);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el espacio en Blanco en el campo Nº título de familia
	 * numerosa
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 12)
	public ValueForm checkValueBlankNumFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBlankNumFamilyTitle");
		}
		// Asegurarse de borrar el campo
		fillFamilyExemption();
		clearNumFamilyTitle();

		// Se establece el espacio en blanco
		String value = Constants.BLANK_SPACE;
		
		By numFamilyTitleBy = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_NUM_FAMILY_TITLE)
				+ "')]]/following-sibling::td/input");

		// Se establece el valor
		untilSendKeysElement(numFamilyTitleBy, value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NUM_FAMILY_TITLE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NUM_FAMILY_TITLE),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_NUM_FAMILY_TITLE, FunctionalConstants.FAMILIA_TITUL, false);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, untilGetAttributeElement(numFamilyTitleBy, Constants.VALUE));

		return valueForm;
	}
	/**************************************************************************
	 * FIN CHECK EMPTY METODOS
	 **************************************************************************/

	/**************************************************************************
	 * INICIO CHECK METODOS
	 **************************************************************************/

	/**
	 * Método que valida los frames de la página
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkFrames() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkFrames - PASO: 4");
		}

		// Se comprueba las secciones de la página
		List<WebElement> listFrames = untilFindElements(By.xpath("//*[local-name()='h3']"));
		if ((listFrames == null) || (listFrames.isEmpty()) || (listFrames.size() != 3)) {
			AssertJUnit.assertFalse(true);
		} else {
			AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.FRAME_MESSAGE_USER),
					listFrames.get(0).getText());
			AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.FRAME_MESSAGE_CALL),
					listFrames.get(1).getText());
			AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.FRAME_MESSAGE_RATES),
					listFrames.get(2).getText());
		}
	}

	/**
	 * Método que valida la convocatoria seleccionada
	 * 
	 * @param userData
	 *            Datasource con las diferentes convocatorias
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkCodeCall(UserData userData) throws Exception {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkCodeCall - PASO: 4");
		}

		// Se sacan los datos de las convocatorias
		String[] tokens = userData.getUserName().split(" - ");
		String code = "";
		String call = "";
		String accessType = "";
		if ((tokens == null) || (tokens.length != 3)) {
			AssertJUnit.assertFalse(true);
		} else {
			code = tokens[0].toString().trim();
			AssertJUnit.assertEquals(code,
					getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
							+ PropertiesFile.getValue(FunctionalConstants.VALUE_CODE)
							+ "')]]/following-sibling::td/textarea")).getText());
			call = tokens[1].toString().trim();
			AssertJUnit.assertEquals(call,
					getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
							+ PropertiesFile.getValue(FunctionalConstants.VALUE_CALL)
							+ "')]]/following-sibling::td/textarea")).getText());
			accessType = tokens[2].toString().trim();
			AssertJUnit.assertEquals(accessType,
					getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
							+ PropertiesFile.getValue(FunctionalConstants.VALUE_ACCESS_TYPE)
							+ "')]]/following-sibling::td/textarea")).getText());
		}
	}

	/**
	 * Chequear el País
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkCountry() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkCountry");
		}

		String value = PropertiesFile.getValue(FunctionalConstants.DATA_SPAIN);
		
		By locator = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY)
				+ "')]]/following-sibling::td/div/input");
		
		// Se comprueba que se han modificado el País
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE, value));

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_COUNTRY), FunctionalConstants.ERROR_MESSAGE_COUNTRY,
				null, true);
	}

	/**
	 * Chequear el País se limpia
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkClearCountry() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkClearCountry");
		}

		String value = Constants.CADENA_VACIA;

		// Se comprueba que se han modificado el País
		AssertJUnit.assertEquals(value, untilGetAttributeElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
						+ PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY)
						+ "')]]/following-sibling::td/div/input"), Constants.VALUE));

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_COUNTRY), FunctionalConstants.ERROR_MESSAGE_COUNTRY,
				null, true);
	}

	/**
	 * Se chequea el código postal como no obliogatorio
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkNotRequiredPostalCode() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkNotRequiredPostalCode");
		}

		String value = untilGetAttributeElement(By.id("generalform:accordionPanel:cp"), Constants.VALUE);

		// Se verifica que el campo CP no sea obligatorio
		AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_NOT_REQUIRED_POSTALCODE),
				untilGetTextElement(By
						.xpath("//*[local-name()='td'][input [contains(@id,'generalform:accordionPanel:cp')]]/preceding-sibling::td/span")));

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_POSTALCODE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_POSTALCODE),
				FunctionalConstants.ERROR_MESSAGE_POSTAL_CODE, FunctionalConstants.SOLICITANTE_CP, false);
	}

	/**
	 * Se chequea el código postal como obligatorio
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkRequiredPostalCode() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkRequiredPostalCode");
		}

		String value = untilGetAttributeElement(By.id("generalform:accordionPanel:cp"), Constants.VALUE);

		// Se verifica que el campo CP es obligatorio
		AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_REQUIRED_POSTALCODE),
			untilGetTextElement(By.xpath("//*[local-name()='td'][input [contains(@id,'generalform:accordionPanel:cp')]]/preceding-sibling::td/span")));

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_POSTALCODE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_POSTALCODE),
				FunctionalConstants.ERROR_MESSAGE_POSTAL_CODE, FunctionalConstants.SOLICITANTE_CP, true);
	}

	/**
	 * Se chequea que el centro base como obligatorio
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkRequiredBaseCenter() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkRequiredBaseCenter");
		}

		// Se verifica que el campo Centro Base es obligatorio
		AssertJUnit
		.assertEquals(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_BASE_CENTER),
				untilGetTextElement(By.xpath("//*[local-name()='span'][text() [contains(.,'"
								+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_BASE_CENTER) + "')]]")));
		
		String value = untilGetAttributeElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
						+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_BASE_CENTER)
						+ "')]]/following-sibling::td/input"), Constants.VALUE);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BASE_CENTER), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_BASE_CENTER),
				FunctionalConstants.ERROR_MESSAGE_BASE_CENTER, FunctionalConstants.CENTRO_BASE, true);
	}

	/**
	 * Se chequea que el Nº título de familia numerosa es obligatorio
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkRequiredNumFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkRequiredNumFamilyTitle");
		}

		// Se verifica que el campo Nº título de familia numerosa es obligatorio
		AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_NUM_FAMILY_TITLE),
				untilGetTextElement(By.xpath("//*[local-name()='span'][text() [contains(.,'"
						+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_NUM_FAMILY_TITLE) + "')]]")));
	
		String value = untilGetAttributeElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
						+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_NUM_FAMILY_TITLE)
						+ "')]]/following-sibling::td/input"), Constants.VALUE);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NUM_FAMILY_TITLE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NUM_FAMILY_TITLE),
				FunctionalConstants.ERROR_MESSAGE_NUM_FAMILY_TITLE, FunctionalConstants.FAMILIA_TITUL, true);
	}

	/**
	 * Se chequea que la fecha de caducidad del título es obligatoria
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void checkRequiredDateFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkRequiredDateFamilyTitle");
		}

		// Se verifica que la fecha de caducidad del título es obligatoria
		AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DATE_FAMILY_TITLE),
				untilGetTextElement(By.xpath("//*[local-name()='span'][text() [contains(.,'"
						+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DATE_FAMILY_TITLE) + "')]]")));
		
		String value = untilGetAttributeElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DATE_FAMILY_TITLE)
				+ "')]]/following-sibling::td/span/input"), Constants.VALUE);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DATE_FAMILY_TITLE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_DATE_FAMILY_TITLE),
				FunctionalConstants.ERROR_MESSAGE_DATE_FAMILY_TITLE, FunctionalConstants.FECHA_CADUC, true);
	}

	/**
	 * Método que chequea un NIF erroneo
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 1)
	public ValueForm checkValueErrorNIF() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueErrorNIF");
		}

		// Asegurarse en borrar el campo
		clearNIF();

		// Se establece el DNI erroneo
		String value = "33540149Q";

		// Se establece el NIF
		By locator = By.id("generalform:accordionPanel:nifNie");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NIF), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NIF), FunctionalConstants.ERROR_MESSAGE_FORMAT_NIF,
				FunctionalConstants.SOLICITANTE_NIF, true);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se ha modificado el DNI
		AssertJUnit.assertEquals(value,
				untilGetAttributeElement(locator, Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea un NIE sin marcar ninguna de las causas que dan
	 * derecho a presentarse
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 2)
	public ValueForm checkValueWithoutCauseNIE() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueWithoutCauseNIE");
		}

		// Asegurarse en borrar el campo
		clearNIF();

		// Se establece el NIE
		setNie(GeneratorRandomNIE.generateNIE());

		// Se establece el NIF
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:nifNie"));
		webElement.sendKeys(getNie());

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NIF), getNie(),
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CAUSE_NIF),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_CAUSE_NIE, FunctionalConstants.SOLICITANTE_NIF, true);

		// Se comprueba que se ha modificado el NIE
		AssertJUnit.assertEquals(getNie(), webElement.getAttribute(Constants.VALUE));

		// Se comprueba que se muestran las causas de presentación
		if (!isPresentCauseNie()) {
			LOGGER.error(
					"WebDriverSelenium.checkValueWithoutCauseNIE: No se muestra en la pantalla las causas de Presentación");
			AssertJUnit.assertFalse(true);
		}

		return valueForm;
	}

	/**
	 * Método que verifica aparecen las causas de presentación
	 * 
	 * @throws Exception
	 */
	public boolean isPresentCauseNie() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.isPresentCauseNie");
		}

		boolean isPresent = getDriver().findElement(By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.MESSAGE_CAUSE_NIE) + "')]]")).isDisplayed();

		return isPresent;
	}

	/**
	 * Método que chequea si el código postal corresponde con la provincia
	 * seleccionada
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 3)
	public ValueForm checkValuePostalCodeProvince() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValuePostalCodeProvince");
		}

		// Asegurarse de borrar los campos
		clearProvice();
		clearPostalCode();

		// Se indica la provimcia (Default: Valladolid)
		fillProvice();

		String value = "27671";

		// Se establece el Código Postal
		By locator = By.id("generalform:accordionPanel:cp");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_POSTALCODE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_POSTALCODE),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_POSTAL_CODE_PROVINCE, FunctionalConstants.SOLICITANTE_CP, true);

		// Se comprueba que se han modificado el Código Postal
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el formato de la fecha de Nacimiento
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 4)
	public ValueForm checkValueBirthDate() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueBirthDate");
		}

		// Asegurarse del campo
		clearBirthDate();

		String value = "1982/02/14";

		// Se establece la Fecha de Nacimiento
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:fechaNacimiento_input"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BIRTHDATE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_BIRTHDATE),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_BIRTHDATE, FunctionalConstants.SOLICITANTE_FECHN, true);

		// Se comprueba que se han modificado la Fecha de Nacimiento
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea si se cumple la edad mínima para incribirse en una
	 * convocatoria
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 5)
	public ValueForm checkValueMinBirthDate() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueMinBirthDate");
		}

		// Asegurarse del campo
		clearBirthDate();

		String value = DateUtils.getNowDateString(Constants.DATE_MIN_FORMAT);

		// Se establece la Fecha de Nacimiento
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:fechaNacimiento_input"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BIRTHDATE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_MIN_BIRTHDATE),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_MIN_BIRTHDATE, FunctionalConstants.SOLICITANTE_FECHN, true);

		// Se comprueba que se han modificado la Fecha de Nacimiento
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el formato del email
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 6)
	public ValueForm checkValueEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueEmail");
		}

		// Limpiar del campo
		clearEmail();

		String value = "prueba_email.es";

		// Se establece el email
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:email"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_EMAIL), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_ERROR_EMAIL),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, false);

		// Se comprueba que se ha modificado el email
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el formato del email de confirmación
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 7)
	public ValueForm checkValueConfirmEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueConfirmEmail");
		}

		String value = "prueba_email.es";

		// Se establece el email de confirmación
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:email2"));
		webElement.clear();

		// Se establece el email de confirmación
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CONFIRM_EMAIL), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CONFIRM_ERROR_EMAIL),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_CONFIRM_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, false);

		// Se comprueba que se ha modificado el email
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea que el campo confirmación de email no se encuentre
	 * vación cuando se ha informado el campo Correo Electrónico
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 8)
	public ValueForm checkValueEmptyConfirmEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueEmptyConfirmEmail");
		}
		// Asegurarse en borrar el campo
		clearConfirmEmail();

		// Se establece a campo vacío
		String value = Constants.CADENA_VACIA;

		// Se establece el email de confirmación
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:email2"));
		webElement.sendKeys(value);

		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CONFIRM_EMAIL), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CONFIRM_EMAIL),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_REWRITE_CONFIRM_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, true);

		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea que si el email y el de confirmación son idénticos
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 9)
	public ValueForm checkValueCompareEmails() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueCompareEmails");
		}

		clearEmail();
		fillEmail();

		// Se establece un correo diferente a dlago.asitec@saludcastillayleon.es
		String value = "prueba@saludcastillayleon.es";

		// Necesario para que encuentre el elemento
		untilClearElement(By.id("generalform:accordionPanel:email2"));
		
		// Se establece el email de confirmación
		untilSendKeysElement(By.id("generalform:accordionPanel:email2"), value);
		
		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CONFIRM_EMAIL), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CONFIRM_EMAIL),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_COMPARE_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, true);
		
		//Se cambia el foco para validar
		focus();
		
		// Se comprueba que se ha modificado el campo
		AssertJUnit.assertEquals(value, untilGetAttributeElement(By.id("generalform:accordionPanel:email2"), Constants.VALUE));
		
		return valueForm;
	}


	/**
	 * Chequea el formato del porcentaje de discapacidad
	 * 
	 * @throws Exception
	 *             Exc epción producida
	 */
	@Settings(order = 10)
	public ValueForm checkValueDisability() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueDisability");
		}

		clearDisability();

		String value = "1111,0";

		// Se indica un porcentaje de discapacidad
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:number"));
		webElement.sendKeys(value);

		boolean required = false;
		if (accessTypeCall.equals(AccessType.DISABILITY_FREE)
				|| accessTypeCall.equals(AccessType.DISABILITY_INTERNAL)) {
			required = true;
		}
		// Add campo a lista
		ValueForm valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DISABILITY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_DISABILITY),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_DISABILITY, FunctionalConstants.DISCAPACIDAD, required);

		// Se comprueba que se ha modificado el porcentaje de discapacidad
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));

		return valueForm;
	}

	/**
	 * Método que chequea el formato de la fecha de caducidad del título
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 12, postAction = Settings.PostActionType.FOCUS)
	public ValueForm checkValueDateFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.checkValueDateFamilyTitle");
		}

		// Asegurarse de borrar campo
		clearDateFamilyTitle();

		ValueForm valueForm = null;

		// Si está activo se limpia el campo
		if (isActiveFamilyExemption()) {
			String value = "1982/02/14";
			
			By dateFamilyTitleBy = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DATE_FAMILY_TITLE)
					+ "')]]/following-sibling::td/span/input");
			
			// Se establece el valor
			untilSendKeysElement(dateFamilyTitleBy, value);

			// Add campo a lista
			valueForm = addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DATE_FAMILY_TITLE), value,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_FORMAT_DATE_FAMILY_TITLE),
					FunctionalConstants.ERROR_MESSAGE_FORMAT_DATE_FAMILY_TITLE, FunctionalConstants.FECHA_CADUC, false);

			// Se comprueba que se han modificado el campo
			AssertJUnit.assertEquals(value, untilGetAttributeElement(dateFamilyTitleBy, Constants.VALUE));

			// Se cambia el foco
			focus();
		}

		return valueForm;
	}

	/**************************************************************************
	 * FIN CHECK METODOS
	 **************************************************************************/

	/**************************************************************************
	 * INICIO FILL METODOS
	 **************************************************************************/
	/**
	 * Método que establece el NIF
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 1)
	public void fillNIF() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillNIF");
		}

		if (requireDuplicateCall) {
			setDni("33540149P");
		} else {
			// Se establece el DNI
			setDni(GeneratorRamdomDNI.generateDNI());
		}
		
		WebElement webElement =	getDriver().findElement(By.id("generalform:accordionPanel:nifNie"));
		webElement.sendKeys(getDni());
		
		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NIF), getDni(),
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NIF), FunctionalConstants.ERROR_MESSAGE_NIF, 
				FunctionalConstants.SOLICITANTE_NIF, true);

		// Se comprueba que se ha modificado el DNI
		AssertJUnit.assertEquals(getDni(), webElement.getAttribute(Constants.VALUE));

		// Se comprueba que no aparecen las causas de presentación
		if (isPresentCauseNie()) {
			LOGGER.error("WebDriverSelenium.fillNIF: Se muestra en la pantalla las causas de Presentación");
			AssertJUnit.assertFalse(true);
		}
		
	}

	/**
	 * Método que establece los apellidos.
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 2)
	public void fillSurname() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillSurname");
		}
		String value = "LAGO";

		// Se establece los Apellidos
		By locator  = By.id("generalform:accordionPanel:apellidos");
		untilSendKeysElement(locator, value);
		
		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_SURNAME), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_SURNAME), FunctionalConstants.ERROR_MESSAGE_SURNAME,
				FunctionalConstants.SOLICITANTE_APELLIDOS, true);

		// Se comprueba que se han modificado los apellidos
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));
	}

	/**
	 * Método que establece el Nombre
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 3)
	public void fillName() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillName");
		}
		String value = "DIEGO";

		// Se establece el nombre
		By locator = By.id("generalform:accordionPanel:nombre");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NAME), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME), FunctionalConstants.ERROR_MESSAGE_NAME,
				FunctionalConstants.SOLICITANTE_NOM, true);

		// Se comprueba que se han modificado el nombre
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));
	}

	/**
	 * Método que establece la Fecha de Nacimiento
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 4)
	public void fillBirthDate() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillBirthDate");
		}

		String value = "14/02/1982";

		// Se establece la Fecha de Nacimiento
		By locator = By.id("generalform:accordionPanel:fechaNacimiento_input");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BIRTHDATE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_BIRTHDATE),
				FunctionalConstants.ERROR_MESSAGE_BIRTHDATE, FunctionalConstants.SOLICITANTE_FECHN, true);

		// Se comprueba que se han modificado la Fecha de Nacimiento
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));
	}

	/**
	 * Método que establece la nacionalidad
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 5)
	public void fillNacionality() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillNacionality");
		}

		String value = "ESPAÑOLA";

		// Se establece la Nacionalidad
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:nacionalidad"));
		webElement.sendKeys(value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NACIONALITY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NACIONALITY),
				FunctionalConstants.ERROR_MESSAGE_NACIONALITY, FunctionalConstants.SOLICITANTE_NAC, true);

		// Se comprueba que se han modificado la Nacionalidad
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Método que establece la dirección
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 6)
	public void fillAddress() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillAddress");
		}

		String value = "PRUEBA";

		// Se establece la Dirección
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:direccion"));
		webElement.sendKeys(value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_ADDRESS), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_ADDRESS), FunctionalConstants.ERROR_MESSAGE_ADDRESS,
				FunctionalConstants.SOLICITANTE_DIR, true);

		// Se comprueba que se han modificado la Dirección
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Establece la provincia "Otra"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 7)
	public void fillOtherProvice() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillOtherProvice");
		}
		String value = PropertiesFile.getValue(FunctionalConstants.MESSAGE_OTHER_PROVINCE);

		// Se establece la Provincia
		By locator = By.id("generalform:accordionPanel:provincias_label");
		untilClickElement(locator);
			
		//Comprobar que esta display el acordion
		WebElement panel = untilFindElement(By.id("generalform:accordionPanel:provincias_panel"));
		if (!panel.isDisplayed()) {
			untilClickElement(locator);
		}
		
		// Se establece la el valor de la provincia		
		untilSelectElement(By.xpath("//*[local-name()='ul'][@id='generalform:accordionPanel:provincias_items']/*[local-name()='li']"), value);
	
		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_PROVINCE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME_PROVINCE),
				FunctionalConstants.ERROR_MESSAGE_NAME_PROVINCE, FunctionalConstants.SOLICITANTE_PROV, true);

		// Se comprueba que se ha modificado la Provincia
		AssertJUnit.assertEquals(value, untilGetTextElement(locator));
		
		//Cambiamos el foco
		focus();

		// Verificamos que se limpie el País.
		checkClearCountry();

		// Verificamos que el Codigo Postal no es obligatorio
		checkNotRequiredPostalCode();
	}

	/**
	 * Método que rellena el nombre de la provincia cuando se selecciona la
	 * opción "Otra"
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 8)
	public void fillNameProvince() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillNameProvince");
		}

		// Se limpia la Provincia
		clearNameProvince();

		// Se establece una pronvincia que no es Española
		String value = "Oporto";

		untilSendKeysElement(By.id("generalform:accordionPanel:otraProvincia"), value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NAME_PROVINCE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME_PROVINCE),
				FunctionalConstants.ERROR_MESSAGE_NAME_PROVINCE, FunctionalConstants.SOLICITANTE_PROV, false);

		//Se cambia el foco
		focus();
		
		// Se comprueba que se ha modificado la otra provincia
		AssertJUnit.assertEquals(value,
				untilGetAttributeElement(By.id("generalform:accordionPanel:otraProvincia"), Constants.VALUE));

		// Se establece "Otra" Provincia como informado
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_PROVINCE),
				PropertiesFile.getValue(FunctionalConstants.MESSAGE_OTHER_PROVINCE),
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NAME_PROVINCE),
				FunctionalConstants.ERROR_MESSAGE_NAME_PROVINCE, 
				FunctionalConstants.SOLICITANTE_PROV, false);
	}

	/**
	 * Método que establece el municipio
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 9)
	public void fillCity() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillCity");
		}

		String value = "ARROYO DE LA ENCOMIENDA";

		// Se establece el Municipio
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:municipio"));
		webElement.sendKeys(value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CITY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CITY), FunctionalConstants.ERROR_MESSAGE_CITY,
				FunctionalConstants.SOLICITANTE_MUN, true);

		// Se comprueba que se han modificado el Municipio
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Establece la provincia
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 10)
	public void fillProvice() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillProvice");
		}
		String value = "Valladolid";

		// Se establece la Provincia
		//Comprobar que esta display el acordion
		By locator = By.id("generalform:accordionPanel:provincias_label");
		WebElement panel = untilFindElement(locator);
		if (!panel.isSelected()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("WebDriverSelenium.fillProvice - panel.isDisplayed()");
			}
			untilClickElement(locator);
		}
				
		// Se establece la el valor de la provincia		
		untilSelectElement(By.xpath("//*[local-name()='ul'][@id='generalform:accordionPanel:provincias_items']/*[local-name()='li']"), value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_PROVINCE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_PROVINCE),
				FunctionalConstants.ERROR_MESSAGE_PROVINCE, FunctionalConstants.SOLICITANTE_PROV, true);

		AssertJUnit.assertEquals(value, untilGetTextElement(locator));
		
		//Cambiamos el foco
		focus();
		
		//Se espera hasta que el la página este activa
		waitUntilDocumentIsReady();

		// Verificamos que se rellene el País.
		checkCountry();

		// Verificamos que el Codigo Postal es obligatorio
		checkRequiredPostalCode();
	}

	/**
	 * Establece el código postal
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 11)
	public void fillPostalCode() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillPostalCode");
		}

		String value = "47195";

		// Se establece el Código Postal
		By locator = By.id("generalform:accordionPanel:cp");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_POSTALCODE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_POSTALCODE),
				FunctionalConstants.ERROR_MESSAGE_POSTAL_CODE, FunctionalConstants.SOLICITANTE_CP, true);

		// Se comprueba que se han modificado el Código Postal
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE, value));
	}

	/**
	 * Establece el País
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 12)
	public void fillCountry() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillCountry");
		}

		String value = PropertiesFile.getValue(FunctionalConstants.DATA_SPAIN);

		// Se limpia el País
		clearCountry();
		
		// Se establece el País
		By locator = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
				+ PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY) + "')]]/following-sibling::td/div/input");
		untilSendKeysElement(locator, value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_COUNTRY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_COUNTRY), FunctionalConstants.ERROR_MESSAGE_COUNTRY,
				null, true);

		// Se comprueba que se han modificado el País
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE, value));
	}

	/**
	 * Establece la titulaciópn
	 * 
	 * @throws Exception
	 *             Exc epción producida
	 */
	@Settings(order = 13)
	public void fillDegree() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillDegree");
		}

		String value = "INGENIERO";

		// Se establece la Titulación
		WebElement webElement = getDriver().findElement(By.id("generalform:accordionPanel:titulacion"));
		webElement.sendKeys(value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DEGREE), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_DEGREE), FunctionalConstants.ERROR_MESSAGE_DEGREE,
				FunctionalConstants.SOLICITANTE_TITUL, true);

		// Se comprueba que se han modificado la Titulación
		AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));
	}

	/**
	 * Establece la Provincia de Examen
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 14)
	public void fillExamProvice() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillExamProvice");
		}
		
		// Se establece la Provincia de Examen
		By locator = By.id("generalform:accordionPanel:provinciaSelect_label");
		untilClickElement(locator);
		
		// Se establece el primer elemento del select ya que a veces no existen
		// las mismas provincias
		String provinciaSelect = "";
		
		WebElement webElementInput = untilFindElement(By.id("generalform:accordionPanel:provinciaSelect_input"));
		webElementInput.isEnabled();
		Select select = new Select(webElementInput);
		List<WebElement> options = select.getOptions();
		if ((options == null) || (options.isEmpty())) {
			AssertJUnit.assertFalse(true);
		} else {
			for (WebElement option : options) {
				if (!PropertiesFile.getValue(FunctionalConstants.MESSAGE_EXAM_PROVINCE).equalsIgnoreCase(
						option.getAttribute(Constants.VALUE))) {
					provinciaSelect = option.getAttribute(Constants.VALUE);
					break;
				}
			}
			
			untilClickElement(By.xpath(
					"//li[contains(@id,':provinciaSelect_') and contains(@data-label,'" + provinciaSelect + "')]"));
			
			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_EXAM_PROVINCE), provinciaSelect,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_EXAM_PROVINCE),
					FunctionalConstants.ERROR_MESSAGE_EXAM_PROVINCE, FunctionalConstants.PROVINCIA_EXAMEN, false);
			
			//Se cambia el foco
			focus();
						
			// Se comprueba que se han modificado la Provincia de Examen
			AssertJUnit.assertEquals(provinciaSelect, untilGetTextElement(locator));
		}
	}

	/**
	 * Establece un porcentaje de discapacidad (tipo de accesso con
	 * discapacidad)
	 * 
	 * @throws Exception
	 *             Excepción producida access =
	 *             Settings.AccessType.DISABILITY_FREE
	 */
	@Settings(order = 15)
	public void fillDisabilityPercent() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillDisabilityPercent");
		}

		clearDisability();

		String value = "33,0";
		
		// Se indica un porcentaje de discapacidad
		untilSendKeysElement(By.id("generalform:accordionPanel:number"), value);
	
		boolean required = false;
		if (accessTypeCall.equals(AccessType.DISABILITY_FREE)
				|| accessTypeCall.equals(AccessType.DISABILITY_INTERNAL)) {
			required = true;
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DISABILITY), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_DISABILITY),
				FunctionalConstants.ERROR_MESSAGE_DISABILITY_PERCENT, 
				FunctionalConstants.DISCAPACIDAD, required);

		// Se comprueba que se ha modificado el porcentaje de discapacidad
		AssertJUnit.assertEquals(value, untilGetAttributeElement(By.id("generalform:accordionPanel:number"), Constants.VALUE));
	}

	/**
	 * Establece la categoría de origen (promoción interna)
	 * 
	 * @throws Exception
	 *             Excepción producida access = Settings.AccessType.ORD_INTERNAL
	 */
	@Settings(order = 16)
	public void fillCategoryOrigin() throws Exception {

		if (accessTypeCall.equals(AccessType.ORD_INTERNAL) || accessTypeCall.equals(AccessType.DISABILITY_INTERNAL)) {

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("WebDriverSelenium.fillCategoryOrigin");
			}
			String value = "Administrativo";

			// Se establece la Categoría de origen
			WebElement webElement = getDriver()
					.findElement(By.id("generalform:accordionPanel:categoriaDeProcedencia_label"));
			webElement.click();
			getDriver().findElement(By.xpath("//li[@data-label='" + value + "']")).click();

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.MESSAGE_CATEGORY_ORIGIN), value,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_CATEGORY_ORIGIN_TITLE),
					FunctionalConstants.ERROR_MESSAGE_CATEGORY_ORIGIN_TITLE, 
					FunctionalConstants.SOLICITANTE_CATEG, false);

			// Se comprueba que se han modificado la Categoría de origen
			AssertJUnit.assertEquals(value, webElement.getText());
		}
	}

	/**
	 * Método que completa un email válido
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 17)
	public void fillEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillEmail");
		}
		String value = "dlago.asitec@saludcastillayleon.es";
		
		// Se establece el email
		By locator = By.id("generalform:accordionPanel:email");
		untilSendKeysElement(locator, value);
		
		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_EMAIL), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_ERROR_EMAIL),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, false);

		// Se comprueba que se ha modificado el email
		AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE, value));
	}

	/**
	 * Método que completa la confirmación del email válido
	 * 
	 * @throws Devuelve
	 *             los datos introducidos en la prueba
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 18)
	public void fillConfirmEmail() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillConfirmEmail");
		}
		String value = "dlago.asitec@saludcastillayleon.es";

		untilClearElement(By.id("generalform:accordionPanel:email2"));
		
		// Se establece el email de confirmación
		Boolean make = untilSendKeysElement(By.id("generalform:accordionPanel:email2"), value);

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_CONFIRM_EMAIL), value,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_CONFIRM_ERROR_EMAIL),
				FunctionalConstants.ERROR_MESSAGE_FORMAT_CONFIRM_EMAIL, FunctionalConstants.SOLICITANTE_EMAIL, false);
		
		// Se comprueba el resultado
		if (make) {
			AssertJUnit.assertEquals(value, untilGetAttributeElement(By.id("generalform:accordionPanel:email2"), Constants.VALUE));
		}
	}

	/**
	 * Establece el check "Exención por discapacidad igual o mayor al 33%"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 19)
	public void fillDisabilityExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillDisabilityExemption");
		}

		// Si no está activo
		if (!isActiveDisabilityExemption()) {
			// Se chequea en el campo label
			By locatorClick = By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DISABILITY_EXEMPTION) + "')]]");
			By locatorCheck = By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DISABILITY_EXEMPTION)
					+ "')]]/preceding-sibling::td/div/div/input");
			
			untilClickCheck(locatorClick, locatorCheck);
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DISABILITY_EXEMPTION), Constants.TRUE,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_BASE_CENTER),
				FunctionalConstants.ERROR_MESSAGE_BASE_CENTER, FunctionalConstants.DISCAPACIDAD, false);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se ha modificado el check
		AssertJUnit.assertEquals(true, isActiveDisabilityExemption());

		// Verificamos que el Centro Base es obligatorio
		checkRequiredBaseCenter();
	}

	/**
	 * Establece el Centro Base
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 20)
	public void fillBaseCenter() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillBaseCenter");
		}

		// Si está activo
		if (isActiveDisabilityExemption()) {

			// Se establece el Centro Base
			String value = "CENTRO BASE";

			// Se establece el Centro Base
			WebElement webElement = getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_BASE_CENTER)
					+ "')]]/following-sibling::td/input"));
			webElement.sendKeys(value);

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_BASE_CENTER), value,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_BASE_CENTER),
					FunctionalConstants.ERROR_MESSAGE_BASE_CENTER, FunctionalConstants.CENTRO_BASE, true);

			// Se comprueba que se ha modificado el campo
			AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));
		}
	}

	/**
	 * Establece el check "Exención por familia numerosa"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 21)
	public void fillFamilyExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillFamilyExemption");
		}

		// Si no está activo
		if (!isActiveFamilyExemption()) {
			// Se chequea en el campo label
			By locatorClick = By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_FAMILY_EXEMPTION) + "')]]");
			By locatorCheck = By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_FAMILY_EXEMPTION)
					+ "')]]/preceding-sibling::td/div/div/input");
			
			untilClickCheck(locatorClick, locatorCheck);
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_FAMILY_EXEMPTION), Constants.TRUE,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_NUM_FAMILY_TITLE),
				FunctionalConstants.ERROR_MESSAGE_NUM_FAMILY_TITLE, FunctionalConstants.FAMILIA_NUM, false);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se han modificado el check "Exención por familia
		// numerosa"
		AssertJUnit.assertEquals(true, isActiveFamilyExemption());

		// Verificamos que el Nº título de familia numerosa (*) es obligatorio
		checkRequiredNumFamilyTitle();

		// Verificamos que el Fecha de caducidad del título (*) es obligatorio
		checkRequiredDateFamilyTitle();
	}

	/**
	 * Establece el Nº título de familia numerosa
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 22)
	public void fillNumFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillNumFamilyTitle");
		}

		// Si está activo
		if (isActiveFamilyExemption()) {

			// Se establece el Nº título de familia numerosa
			String value = "47/0002/02";
			
			By locator = By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_NUM_FAMILY_TITLE)
					+ "')]]/following-sibling::td/input");

			// Se establece el valor
			untilSendKeysElement(locator, value);

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_NUM_FAMILY_TITLE), value,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_NUM_FAMILY_TITLE),
					FunctionalConstants.ERROR_MESSAGE_FORMAT_NUM_FAMILY_TITLE, FunctionalConstants.FAMILIA_TITUL, false);

			// Se comprueba que se ha modificado el Nº título de familia
			// numerosa
			AssertJUnit.assertEquals(value, untilGetAttributeElement(locator, Constants.VALUE));
		}
	}

	/**
	 * Establece la Fecha de caducidad del título
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 23)
	public void fillDateFamilyTitle() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillDateFamilyTitle");
		}

		// Si está activo
		if (isActiveFamilyExemption()) {

			WebElement webElement = getDriver().findElement(By.xpath("//*[local-name()='td'][span/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_DATE_FAMILY_TITLE)
					+ "')]]/following-sibling::td/span/input"));

			String value = "14/02/1982";

			// Se establece la Fecha de caducidad del título
			webElement.sendKeys(value);

			// Add campo a lista
			addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DATE_FAMILY_TITLE), value,
					PropertiesFile.getValue(FunctionalConstants.SUMMARY_FORMAT_DATE_FAMILY_TITLE),
					FunctionalConstants.ERROR_MESSAGE_FORMAT_DATE_FAMILY_TITLE, FunctionalConstants.FECHA_CADUC, false);

			// Se comprueba que se han modificado la fecha de caducidad del
			// título
			AssertJUnit.assertEquals(value, webElement.getAttribute(Constants.VALUE));
		}
	}

	/**
	 * Establece el check
	 * "Los datos relativos a la identidad. - ver apartado 3.4.1. a)"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 24)
	public void fillAuthIdentityDataExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillAuthIdentityDataExemption");
		}

		// Si no está activo
		if (!isActiveAuthIdentityDataExemption()) {
			// Se chequea en el campo label
			By locatorClick = By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_IDENTITY_DATA_TITLE) + "')]]");
			By locatorCheck = By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_IDENTITY_DATA_TITLE)
					+ "')]]/preceding-sibling::td/div/div/input");
			
			untilClickCheck(locatorClick, locatorCheck);
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_IDENTITY_DATA_TITLE), Constants.TRUE,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_AUTH_IDENTITY_DATA_TITLE),
				FunctionalConstants.ERROR_MESSAGE_AUTH_TITLE, FunctionalConstants.AUTORIZACION_IDEN, false);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se ha modificado la opción
		AssertJUnit.assertEquals(true, isActiveAuthIdentityDataExemption());

	}

	/**
	 * Establece el check
	 * "Certificación de no haber sido condenado por sentencia firme por algún delito contra la libertad e indemnidad sexual. (ver apartado 9.2.e)"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 25)
	public void fillAuthCertifCondemnedExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillAuthCertifCondemnedExemption");
		}

		// Si no está activo
		if (!isActiveAuthCertifCondemnedExemption()) {
			// Se chequea en el campo label
			By locatorClick = By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_CERTIF_CONDEMNED_TITLE) + "')]]");
			By locatorCheck = By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_CERTIF_CONDEMNED_TITLE)
					+ "')]]/preceding-sibling::td/div/div/input");
			
			untilClickCheck(locatorClick, locatorCheck);
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_CERTIF_CONDEMNED_TITLE),
				Constants.TRUE, PropertiesFile.getValue(FunctionalConstants.SUMMARY_AUTH_CERTIF_CONDEMNED_TITLE),
				FunctionalConstants.ERROR_MESSAGE_AUTH_TITLE, FunctionalConstants.AUTORIZACION_NOCOND, false);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se ha modificado la opción
		AssertJUnit.assertEquals(true, isActiveAuthCertifCondemnedExemption());

	}

	/**
	 * Establece el check
	 * "Los datos relativos a la discapacidad reconocida en Castilla y León. (ver apartado 3.4.2.c)"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 26)
	public void fillAuthDisabilityExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillAuthDisabilityExemption");
		}

		// Si no está activo
		if (!isActiveAuthDisabilityExemption()) {
			// Se chequea en el campo label
			By locatorClick = By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_DISABILITY_TITLE) + "')]]");
			By locatorCheck = By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_DISABILITY_TITLE)
					+ "')]]/preceding-sibling::td/div/div/input");
			
			untilClickCheck(locatorClick, locatorCheck);
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_DISABILITY_TITLE), Constants.TRUE,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_AUTH_DISABILITY_TITLE),
				FunctionalConstants.ERROR_MESSAGE_AUTH_TITLE, FunctionalConstants.AUTORIZACION_DISCAP, false);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se han modificado la OPCIÓN
		AssertJUnit.assertEquals(true, isActiveAuthDisabilityExemption());
	}

	/**
	 * Establece el check
	 * "Los datos relativos a la condición de familia numerosa reconocida en Castilla y León. (ver apartado 3.4.2.d)"
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	@Settings(order = 27)
	public void fillAuthLargeFamilyExemption() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.fillAuthLargeFamilyExemption");
		}

		// Si no está activo
		if (!isActiveAuthLargeFamilyExemption()) {
			// Se chequea en el campo label
			By locatorClick = By.xpath("//label[text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_LARGE_FAMILY_TITLE) + "')]]");
			By locatorCheck = By.xpath("//*[local-name()='td'][label/text() [contains(.,'"
					+ PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_LARGE_FAMILY_TITLE)
					+ "')]]/preceding-sibling::td/div/div/input");
			
			untilClickCheck(locatorClick, locatorCheck);
		}

		// Add campo a lista
		addMapValues(PropertiesFile.getValue(FunctionalConstants.VALUE_DESCR_AUTH_LARGE_FAMILY_TITLE), Constants.TRUE,
				PropertiesFile.getValue(FunctionalConstants.SUMMARY_AUTH_LARGE_FAMILY_TITLE),
				FunctionalConstants.ERROR_MESSAGE_AUTH_TITLE, FunctionalConstants.AUTORIZACION_FNUM, false);
		
		//Se cambia el foco
		focus();

		// Se comprueba que se han modificado la OPCIÓN
		AssertJUnit.assertEquals(true, isActiveAuthLargeFamilyExemption());
	}

	/**************************************************************************
	 * FIN FILL METODOS
	 **************************************************************************/

	/**
	 * Método que guarda la convocatoria
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void save() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.save");
		}

		// Se guarda el formulario
		focus();

		getDriver().findElement(By.id("generalform:guardar")).click();
		
		// Espera de recarga de página
		getDriver().manage().timeouts().pageLoadTimeout(Constants.TIME_OUT_SECONDS, TimeUnit.SECONDS);
	}
	
	
	/**
	 * Método que guarda la convocatoria y descarga el fichero
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void clickAndSaveFile() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.clickAndSaveFile");
		}

		// Se guarda el formulario
		focus();

		try {
			getDriver().findElement(By.id("generalform:guardar")).click();
		} catch (Exception e) {
			LOGGER.error("Error al Guardar el PDF", e);
		}
	
		if (isBrowserIE()) {
			try {
				
				//Path path = Paths.get(ClassLoader.getSystemResource(Constants.DOWNLOAD_AUTOIT).toURI());
				//Runtime.getRuntime().exec(path.toString());
				
				String hostName = URLProcess.getHostName(ConfigFile.getValue(Constants.REMOTE_WEB_HOST));
				int port = URLProcess.getPort(ConfigFile.getValue(Constants.REMOTE_WEB_HOST));

				String hostNode = GridInfoExtractor.getHostName(hostName, port,((RemoteWebDriver) getDriver()).getSessionId());
				
				String command = "cmd /c D:/Programas/PSTools/PsExec.exe \\" + hostNode + " -s -i C:/Users/dlagoDownloads/Download.exe";
				Runtime.getRuntime().exec(command);
				
				/*
				Path path = Paths.get(ClassLoader.getSystemResource(Constants.DOWNLOAD_AUTOIT).toURI());
				((JavascriptExecutor) getDriver()).executeScript(path.toString());
				*/
				
				//String path = getURLDownload() + File.separator + "Download.exe";
				//Runtime.getRuntime().exec(path);
				
				//((JavascriptExecutor) getDriver()).executeScript(path);
				
				/*
				ProcessBuilder pb = new ProcessBuilder(path.toString());
			    Process p = pb.start();     // Start the process.
			    p.waitFor();
			    */
			    
			} catch (Exception e) {
				LOGGER.error("Error: ", e);
			}
		}
	}


	/**
	 * Método que imprime la convocatoria
	 * 
	 * @throws Exception
	 *             Excepción producida
	 */
	public void print() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.print. PASO 10");
		}

		// Se descarga el formulario
		try {
			getDriver().findElement(By.id("finalform:guardar")).click();
		} catch (Exception e) {
			LOGGER.error("Error al Imprimir el PDF", e);
		}
	
		if (isBrowserIE()) {
			try {
				Path path = Paths.get(ClassLoader.getSystemResource(Constants.DOWNLOAD_AUTOIT).toURI());
				Runtime.getRuntime().exec(path.toString());
			} catch (Exception e) {
				LOGGER.error("Error: ", e);
			}
		}

		// Comprobar la existencia del fichero PDF generado
		existPDFfileRequest();

	}

	/**
	 * Método que comprueba la existencia del fichero PDF generado por la
	 * solicitud
	 * @return Devuelve el nombre del fichero encontrado.
	 * @throws Exception
	 *             Excepción producida
	 */
	public String existPDFfileRequest() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.existPDFfileRequest");
			LOGGER.info("Procesando comprobación existencia PDF generado");
		}

		//Nombre del fichero
		String fileName = null;
		
		//Se comprueba la ruta compartida de los ficheros
		File tmpFile = new File(getURLDownload());
		if (!tmpFile.exists() || !tmpFile.isDirectory()) {
				LOGGER.error(
						"Error el directorio TMP especificado " + tmpFile + " no existe o no es un directorio válido");

			AssertJUnit.assertFalse(true);
		} else {
			// Listamos los que contienen el DNI establecido
			File[] pdfFiles = null;
			
			// Se comprueba el tiempo
			long startTime = System.currentTimeMillis();
			
			while ((pdfFiles == null) || (pdfFiles.length == 0)) {
				
				pdfFiles = tmpFile.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						if (!pathname.isFile())
							return false;

						// Comprobar que son PDF´s con el mismo DNI
						String name = pathname.getName();

						// Si el nombre contiene el DNI y termina 
						// con la extensión .pdf
						if ((name.contains(getDni()) && (name.endsWith(Constants.PDF)))) {
							return true;
						}

						return false;
					}
				});
				
				// Se comprueba el tiempo de espera
				long elapsed = System.currentTimeMillis() - startTime;
				//Se establece 1 minuto para la verificación del fichero
				if (elapsed > (Constants.TIME_OUT_PDF_SECONDS * 1000)) {
					LOGGER.error(
							"Error TIMEOUT. No se han encontrado ficheros para el DNI: " + getDni());
					fileName = null;
					AssertJUnit.assertFalse(true);
				}
			}
			
			// Se establece que se descarge por defecto en Firefox y se
			// comprueba por el nombre del DNI
			if ((pdfFiles == null) || (pdfFiles.length == 0)) {
				LOGGER.error("PDF no encontrado para el DNI: " + getDni());
				fileName = null;
				AssertJUnit.assertFalse(true);
			} else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("WebDriverSelenium.print: Fichero encontrado: " + pdfFiles[0].getName());
				}
				fileName = pdfFiles[0].getName();
				AssertJUnit.assertTrue(true);
			}
		}
		
		return fileName;
	}

	
	/**
	 * Devuelve la URL de descargas de los ficheros PDF
	 * @return URL de descargas de los ficheros PDF
	 * @throws MalformedURLException URL mal formada
	 * @throws UnknownHostException Host desconocido
	 */
	protected String getURLDownload() throws MalformedURLException, UnknownHostException{
		// Se saca el hostName y el Puerto
		String hostName = URLProcess.getHostName(ConfigFile.getValue(Constants.REMOTE_WEB_HOST));
		int port = URLProcess.getPort(ConfigFile.getValue(Constants.REMOTE_WEB_HOST));

		String[] result = GridInfoExtractor.getHostNameAndPort(hostName, port,
				((RemoteWebDriver) getDriver()).getSessionId());

		if (result[0] == null || result[1] == null) {
			LOGGER.error("No se puede obtener el nombre del node que se está ejecutando");
			AssertJUnit.assertFalse(true);
		}

		// Directorio temporal
		return "//" + result[0] + File.separator + ConfigFile.getValue(Constants.REMOTE_DIR);
	}
	
	
	/**
	 * Método que valida mensaje de PDF generado
	 * 
	 * @throws Exception
	 *             Excepción Producida
	 */
	public void validateMessagePDFgenerate() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.validateMessagePDFgenerate");
		}

		if (PropertiesFile.getValue(FunctionalConstants.MESSAGE_PDF_GENERATE)
				.equalsIgnoreCase(getDriver().findElement(By.id("p1")).getText())) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(
						"WebDriverSelenium.save: " + PropertiesFile.getValue(FunctionalConstants.MESSAGE_PDF_GENERATE));
			}
			AssertJUnit.assertTrue(true);
		} else {
			LOGGER.error("Error al salvar la convocatoria: " + getDriver().findElement(By.id("p1")).getText());
			AssertJUnit.assertFalse(true);
		}
	}

	/**
	 * Método que valida mensaje de solicitud existente para el mismo NIF/NIE
	 * 
	 * @throws Exception
	 */
	public boolean validateMessageDuplicateCall() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.validateMessageDuplicateCall");
		}

		if (PropertiesFile.getValue(FunctionalConstants.MESSAGE_DUPLICATE_CALL)
				.equalsIgnoreCase(getDriver()
						.findElement(By
								.xpath("//div[@id='reaccionDialog']/div[@class='ui-dialog-content ui-widget-content']/form/table/tbody/tr/td"))
						.getText())) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("WebDriverSelenium.validateMessageDuplicateCall: "
						+ PropertiesFile.getValue(FunctionalConstants.MESSAGE_DUPLICATE_CALL));
			}
			AssertJUnit.assertTrue(true);
			return true;
		} else {
			LOGGER.error("Error al validar mensaje de solicitud existente para el mismo NIF/NIE");
			AssertJUnit.assertFalse(true);
			return false;
		}
	}

	/**
	 * Método que activa la opción adecuada (Cancelar/Generar) en el mensaje de
	 * solicitud ya existente para el mismso NIF/NIE
	 * 
	 * @param generateCall
	 * @throws Exception
	 */
	public void confirmDuplicateCall(boolean generateCall) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.confirmDuplicateCall");
		}

		if (generateCall) {
			// Hay que pulsar el botón de Generar solicitud PDF
			WebElement webElement = getDriver().findElement(By
					.xpath("//div[@id='reaccionDialog']/div[@class='ui-dialog-content ui-widget-content']/form/table/tbody/tr/td/button[contains(.,'"
							+ PropertiesFile.getValue(FunctionalConstants.BUTTON_GENERATE_CALL) + "')]"));
			webElement.click();

			AssertJUnit.assertTrue(true);
		} else {
			// Hay que pulsar el botón de Cancelar
			WebElement webElement = getDriver().findElement(By
					.xpath("//div[@id='reaccionDialog']/div[@class='ui-dialog-content ui-widget-content']/form/table/tbody/tr/td/button[contains(.,'"
							+ PropertiesFile.getValue(FunctionalConstants.BUTTON_CANCEL) + "')]"));
			webElement.click();

			AssertJUnit.assertEquals(PropertiesFile.getValue(FunctionalConstants.MESSAGE_REMEMBER_DUPLICATE_CALL),
					getDriver().findElement(By.xpath("//div[@class='alert alert-info']")).getText());
		}
	}

	/**
	 * Método para validar el acceso al Modelo 046 de pago de tasas
	 * 
	 * @throws Exception
	 */
	public void validateAccessPaymentModel046() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.validateAccessPaymentModel046");
		}

		WebElement webElement = getDriver().findElement(By.xpath("//form[@id='finalform']/table/tbody/tr/td/a"));

		if (PropertiesFile.getValue(FunctionalConstants.LINK_PAYMENT_CALL).equalsIgnoreCase(webElement.getText())) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("WebDriverSelenium.validateAccessPaymentModel046: "
						+ PropertiesFile.getValue(FunctionalConstants.LINK_PAYMENT_CALL));
			}

			webElement.click();
			
			AssertJUnit.assertTrue(true);
		} else {
			LOGGER.error("Error al acceder al link Acceso al modelo 046");
			AssertJUnit.assertFalse(true);
		}
	}

	/**
	 * Método que una lista de valores vacíos para comprobar los campos
	 * requeridos
	 * 
	 * @return Devuelve la lista de mensajes obligatorios para la convocatoria
	 *         Libre-Ordinario
	 * @throws Exception
	 *             Excepción Producida
	 */
	public List<String> getListEmptyValuesRequired(AccessType accessType) throws Exception {
		List<String> listEmpty = null;
		if ((mapValues != null) && (!mapValues.isEmpty())) {
			Iterator<String> it = mapValues.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				ValueForm valueForm = mapValues.get(key);
				boolean isEmpty = false;
				if (valueForm.isRequired()) {
					if (valueForm.getValue() == null) {
						isEmpty = true;
					} else {
						// Comprobamos si es un valor vacio
						if ((valueForm.getValue().isEmpty())
								|| (PropertiesFile.getValue(FunctionalConstants.MESSAGE_PROVINCE)
										.equals((valueForm.getValue())))
								|| (PropertiesFile.getValue(FunctionalConstants.MESSAGE_EXAM_PROVINCE)
										.equals((valueForm.getValue())))
								|| (PropertiesFile.getValue(FunctionalConstants.MESSAGE_OTHER_PROVINCE)
										.equals((valueForm.getValue())))
								|| (PropertiesFile.getValue(FunctionalConstants.MESSAGE_CATEGORY_ORIGIN)
										.equals((valueForm.getValue())))
								|| (PropertiesFile.getValue(FunctionalConstants.SUMMARY_DISABILITY)
										.equals((valueForm.getValue())))) {
							isEmpty = true;
						}
					}
				}

				if (isEmpty) {
					if (listEmpty == null) {
						listEmpty = new ArrayList<String>();
					}
					if (PropertiesFile.getValue(FunctionalConstants.SUMMARY_DISABILITY).equals((valueForm.getName()))) {
						Object[] parameters = new Object[1];
						if (accessType.equals(AccessType.DISABILITY_FREE)) {
							parameters[0] = Access.LIBRE_DISC.getValue();
						} else if (accessType.equals(AccessType.DISABILITY_INTERNAL)) {
							parameters[0] = Access.PROM_INTER_DISC.getValue();
						}
						listEmpty.add(PropertiesFile.getValueWithParameters(valueForm.getPropertyDetail(), parameters));
					} else {
						listEmpty.add(PropertiesFile.getValue(valueForm.getPropertyDetail()));
					}
				}
			}
		}

		return listEmpty;
	}

	/**
	 * Método que valida los mensajes de error requeridos para las convocatorias
	 * en general
	 * 
	 * @param userData
	 *            DataSource que contiene los diferentes tipos de convocatoria
	 * @throws Exception
	 *             Excepción producida en el método
	 */
	public void requiredValuesGeneralAccessType(UserData userData, AccessType accessType) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.requiredValuesGeneralAccessType");
		}

		List<String> listError = getListEmptyValuesRequired(accessType);

		// Se comprueba los mensajes de campos obligatorios
		List<WebElement> listErrorMengs = getDriver()
				.findElements(By.xpath("//*[local-name()='span'][@class='ui-messages-error-detail']"));

		if (((listErrorMengs == null) || (listErrorMengs.isEmpty()))
				&& (((listError == null) || (listError.isEmpty())))) {
			AssertJUnit.assertTrue(true);
		} else {

			if ((listError != null) && (listError.size() == listErrorMengs.size())) {
				int i = 0;
				for (WebElement textarea : listErrorMengs) {
					if (listError.contains(textarea.getText())) {
						i++;
					} else {
						LOGGER.error("WebDriverSelenium.requiredValuesGeneralAccessType: Valor pantalla: "
								+ textarea.getText());
						break;
					}
				}

				// Si contiene todos los elementos
				if (i == listErrorMengs.size()) {
					AssertJUnit.assertTrue(true);
				} else {
					AssertJUnit.assertFalse(true);
				}

			} else {
				LOGGER.error(
						"WebDriverSelenium.requiredValuesGeneralAccessType: Los campos requeridos no poseen el mismo tamaño en la pantalla que en el mapa");
				for (WebElement textarea : listErrorMengs) {
					LOGGER.error("-------->" + textarea.getText());
				}
				AssertJUnit.assertFalse(true);
			}
		}
	}

	/**
	 * Método que valida los mensajes de error de los formatos de los campos
	 * para las convocatorias en general
	 * 
	 * @param valueForm
	 *            Valores del formulario
	 * @param userData
	 *            Datos de la convocatoria
	 * @throws Exception
	 *             Excepción producida en el método
	 */

	public void errorFormatGeneralAccessType(ValueForm valueForm, UserData userData) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.errorFormatGeneralAccessType: Campo -  " + valueForm.getPropertySummary());
		}
		String msg = "";
		boolean check = false;
		if (FunctionalConstants.ERROR_MESSAGE_FORMAT_MIN_BIRTHDATE.equals(valueForm.getPropertyDetail())) {
			if ((userData.getMinAgeDate() != null) && (userData.getMinAge() != -1)) {
				check = true;
				Object[] parameters = new Object[2];
				parameters[0] = DateUtils.getDateFormatString(userData.getMinAgeDate(), Constants.DATE_MIN_FORMAT);
				parameters[1] = String.valueOf(userData.getMinAge());
				msg = PropertiesFile.getValueWithParameters(valueForm.getPropertyDetail(), parameters);
			}
		} else {
			check = true;
			msg = PropertiesFile.getValue(valueForm.getPropertyDetail());
		}

		if (check) {
			
			// Locator
			By locator = By.xpath("//*[local-name()='span'][(@class='ui-messages-error-summary') and (contains(./text(),'"
							+ valueForm.getPropertySummary() + "'))]/following-sibling::span");

			AssertJUnit.assertEquals(msg, untilGetTextElement(locator));
		}
	}

	/**
	 * Método que invoca a los métodos del patrón
	 * 
	 * @param pattern
	 *            Patrón de métodos a invocar
	 * @throws Exception
	 *             Excepción producida
	 */
	public void invokeMethods(UserData userData, Methods pattern, Settings.AccessType accessTypeCall,
			boolean requireDuplicateCall) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.invokeMethods: " + pattern);
		}

		// load the AppTest at runtime
		Class cls = GeneralForm.class;
		Object obj = cls.newInstance();

		// UserData parameter
		Class[] paramDriver = new Class[1];
		paramDriver[0] = RemoteWebDriver.class;

		// Se inicializan las variables de clase
		Method loadDriver = cls.getMethod(Methods.LOAD_DRIVER.getValue(), paramDriver);
		loadDriver.invoke(obj, getDriver());

		Class[] paramAccessType = new Class[1];
		paramAccessType[0] = Settings.AccessType.class;
		Method setAccessTypeCall = cls.getDeclaredMethod(Methods.LOAD_ACCESS_TYPE_CALL.getValue(), paramAccessType);
		setAccessTypeCall.invoke(obj, accessTypeCall);

		Class[] paramRequireDuplicateCall = new Class[1];
		paramRequireDuplicateCall[0] = boolean.class;
		Method setRequireDuplicateCall = cls.getDeclaredMethod(Methods.LOAD_REQUIRE_DUPLICATE_CALL.getValue(),
				paramRequireDuplicateCall);
		setRequireDuplicateCall.invoke(obj, requireDuplicateCall);

		// UserData parameter
		Class[] paramMap = new Class[1];
		paramMap[0] = Map.class;

		// Se inicializan las variables de clase
		Method setMapValues = cls.getMethod(Methods.SET_MAP_VALUES.getValue(), paramMap);
		setMapValues.invoke(obj, getMapValues());

		// Se sacan todos los metodos declarados
		Method[] allMethods = cls.getDeclaredMethods();

		if ((allMethods != null) && (allMethods.length > 0)) {
			// Ordenar por nombre
			Arrays.sort(allMethods, new Comparator<Method>() {
				@Override
				public int compare(Method o1, Method o2) {
					Settings or1 = o1.getAnnotation(Settings.class);
					Settings or2 = o2.getAnnotation(Settings.class);
					// nulls last
					if (or1 != null && or2 != null) {
						return or1.order() - or2.order();
					} else if (or1 != null && or2 == null) {
						return -1;
					} else if (or1 == null && or2 != null) {
						return 1;
					}
					return o1.getName().compareTo(o2.getName());
				}
			});

			for (Method method : allMethods) {
				String methodName = method.getName();

				// Se procesan los metodos que empiezan por patter
				if (methodName.startsWith(pattern.getValue())) {
					Type[] pType = method.getGenericParameterTypes();
					Settings settings = method.getAnnotation(Settings.class);
					if ((pType.length == 0)) {

						ValueForm valueForm = null;

						if ((settings == null)
								|| ((settings != null) && ((settings.access().equals(Settings.AccessType.ALL))
										|| (settings.access().equals(accessTypeCall))))) {
							valueForm = (ValueForm) method.invoke(obj, null);
						}

						if (Methods.FILL_REQUIRED.equals(pattern)) {

							// Se tiene que tener en cuenta que si es el último
							// registro se crea correctamente
							// En el último registro no se invoca al método
							// save(), pero listError = null y
							// listErrorMengs.count = 1
							List<String> listError = getListEmptyValuesRequired(accessTypeCall);
							if ((listError != null) && (!listError.isEmpty())) {

								// Se guarda
								save();

								// Se comprueban las validaciones
								requiredValuesGeneralAccessType(userData, accessTypeCall);
							} else {
								//focus();
								if (LOGGER.isInfoEnabled()) {
									LOGGER.info(
											"WebDriverSelenium.invokeMethods: La Petición se encuentra correctamente validada");
								}
							}
						} else {
							if (Methods.CHECK_VALUE.equals(pattern)) {

								if ((settings == null) || ((settings != null)
										&& (!settings.postAction().equals(Settings.PostActionType.FOCUS)))) {
									// Se guarda por defecto
									save();
								}

								// Se comprueba los errores de formato.
								if (valueForm != null) {
									errorFormatGeneralAccessType(valueForm, userData);
								}
							}
						}
					}
				}
			}
		}
	}
}
