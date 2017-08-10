package es.ofca.test.psel.functional.activecall;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import es.ofca.test.psel.common.beans.UserData;
import es.ofca.test.psel.common.beans.UsersData;
import es.ofca.test.psel.common.utils.DataProviderUtils;
import es.ofca.test.psel.common.utils.PropertiesFile;
import es.ofca.test.psel.functional.GeneralForm;

/**
 * Clase que permite comprobar si existen convocatorias activas hace referencia
 * al CASO de Test: http://xamp.sacyl.es/jira/browse/PSEL-22
 * 
 * @author dlago
 */
public class CheckActiveCall extends GeneralForm {

	private static final Logger LOGGER = Logger.getLogger(CheckActiveCall.class.getName());

	private static final String MESSAGE_NOT_EXIST_ACTIVE_CALL = "psel.not.exist.active.call";

	/**
	 * Método que valida los mensajes de error requeridos para la convocatoria
	 * Libre-Ordinario
	 * 
	 * @param userData
	 *            DataSource que contiene los diferentes tipos de convocatoria
	 *            Libre Ordinario
	 * @throws Exception
	 *             Excepción producida en el método
	 */
	@Test(enabled = true, priority = 1, groups = { "psel.active.call" })
	public void chekActiveCallForSend() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("WebDriverSelenium.chekActiveCallForSend");
		}
		// Se establece la conexión
		connet();

		// Comprobación de fechas de INICIO

		// Se recogen los usuarios
		UsersData.getInstance();
		Map<String, List<UserData>> mapUsers = UsersData.getMapUsers();

		// Se recogen todas las convocatorias de todos los usuarios

		if ((mapUsers == null) || (mapUsers.isEmpty())) {
			AssertJUnit.assertEquals(PropertiesFile.getValue(MESSAGE_NOT_EXIST_ACTIVE_CALL),
					getDriver().findElement(By.id("p1")).getText());
		} else {
			Iterator<String> it = mapUsers.keySet().iterator();

			List<String> listActiveCon = null;
			boolean showMsg = true;

			while ((it.hasNext())) {
				String key = (String) it.next();

				List<UserData> listConvocatoria = (List<UserData>) mapUsers.get(key);

				if ((listConvocatoria != null) && (!listConvocatoria.isEmpty())) {
					for (UserData convocatoria : listConvocatoria) {
						
						if (DataProviderUtils.isActiveCall(convocatoria)) {
							showMsg = false;

							if (listActiveCon == null) {
								listActiveCon = new ArrayList<String>();
							}

							listActiveCon.add(convocatoria.getUserName());
						}
					}
				}
			}

			if (showMsg) {
				AssertJUnit.assertEquals(PropertiesFile.getValue(MESSAGE_NOT_EXIST_ACTIVE_CALL),
						getDriver().findElement(By.id("p1")).getText());
			} else {

				// Se comprueba el listado de opciones para saber si se
				// encuentran activas
				List<WebElement> listOptions = getDriver().findElements(By.xpath("//*[local-name()='option']"));

				if (((listOptions == null) || (listOptions.isEmpty()))
						&& ((listActiveCon == null) || (listActiveCon.isEmpty()))) {
					AssertJUnit.assertTrue(true);
				} else {
					if (listActiveCon.size() == listOptions.size()) {
						// Comprobar que parece aparece el combo con las convocatorias
						// correctas
						for (WebElement name : listOptions) {
							if (!listActiveCon.contains(name.getAttribute("value"))) {
								AssertJUnit.assertFalse(true);
							}
						}
						
					} else {
						AssertJUnit.assertFalse(true);
					}
				}
			}
		}
	}
}
