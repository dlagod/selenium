package es.ofca.test.psel.common.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.ofca.test.psel.common.beans.UserData;
import es.ofca.test.psel.common.constant.Constants;

/**
 * Clase que crea un Data Provider para Selenium
 * 
 * @author dlago
 */
public class DataProviderUtils {

	private static final Logger LOGGER = Logger.getLogger(DataProviderUtils.class.getName());

	/**
	 * Método que crea un array multidimensional a partir de un mapa de usuarios
	 * hasta una longitud máxima.
	 * 
	 * @param mapUsers
	 *            Mapa de Usuarios
	 * @param max
	 *            Número máximo de usuarios a sacar por rol (con valores
	 *            negativos y cero saca todos).
	 * @param roles
	 *            Listado de Roles a Sacar del mapa de usuarios.s
	 * @return Retorna un array multidimensional a partir de un mapa de usuarios
	 *         hasta una longitud máxima.
	 */
	public static Object[][] createObjectMultiArray(Map<String, List<UserData>> mapUsers, int max, String[] roles) {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("DataProviderUtils.createObjectMultiArray");
		}

		Object[][] result = null;

		if ((mapUsers != null) && (!mapUsers.isEmpty()) && (roles != null) && (roles.length != 0)) {

			List<UserData> finalList = null;

			// Se recorrem los roles
			for (String rol : roles) {
				List<UserData> list = mapUsers.get(rol);
				if ((list != null) && (!list.isEmpty())) {
					if (finalList == null) {
						finalList = new ArrayList<UserData>();
					}

					for (int i = 0; i < list.size(); i++) {
						UserData user = list.get(i);
						if (isActiveCall(user)) {
							finalList.add(user);
						}
		
						if ((max > 0) && (max == (i + 1))) {
							break;
						}
					}
				}
			}

			// Se construye el array
			result = new Object[finalList.size()][1];
			for (int i = 0; i < finalList.size(); i++) {
				result[i][0] = finalList.get(i);
			}
		}

		return result;
	}

	/**
	 * Método que indica si una convocatoria esta o no activa
	 * @param userData Datos de la convocatoria
	 * @return Retorna un valor boolean indicando si la convocatoria está o no activa
	 */
	public static boolean isActiveCall(UserData userData) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("DataProviderUtils.isActiveCall");
		}

		boolean isActive = false;
		
		// Fecha actual
		Date now = DateUtils.getNowDate();

		Date initDate = userData.getInitDate();
		Date endDate = userData.getEndDate();

		if ((initDate.before(now)) && ((endDate.equals(now)) || (endDate.after(now)))) {
			isActive = true;
		} else {
			LOGGER.info("La convocatoria: " + userData.getUserName() + " no está activa. Fecha Fin Publicación: " + DateUtils.getDateFormatString(userData.getEndDate(), Constants.DATE_FORMAT));
		}

		return isActive;
	}

}
