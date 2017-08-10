package es.ofca.test.psel.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Clase de utilidades de Fechas
 * @author dlago
 */
public final class DateUtils {

	/**
	 * Método que devuelve la fecha actual
	 * @return Fecha actual
	 */
	public static Date getNowDate() {
		// Fecha actual
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 00);
		calendar.set(GregorianCalendar.MINUTE, 00);
		calendar.set(GregorianCalendar.SECOND, 00);
		calendar.set(GregorianCalendar.MILLISECOND, 00);
		Date now = calendar.getTime();
		
		return now;
	}
	

	/**
	 * Método que devuelve la fecha actual en función del formato
	 * @param format Formato de la fecha
	 * @return Fecha actual formateada
	 */
	public static String getNowDateString(String format) {
		
		//Formatos de Fecha
		DateFormat formatter = new SimpleDateFormat(format);
		return 	formatter.format(getNowDate());
	}
	
	/**
	 * Método que devuelve la fecha en función del formato
	 * @param date Fecha a formatear
	 * @param format Formato de la fecha
	 * @return Fecha formateada
	 */
	public static String getDateFormatString(Date date, String format) {
		
		//Formatos de Fecha
		DateFormat formatter = new SimpleDateFormat(format);
		return 	formatter.format(date);
	}
}


