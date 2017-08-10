package es.ofca.test.psel.common.beans;
import java.io.Serializable;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import es.ofca.test.psel.common.utils.UserDataDeserializer;
import es.ofca.test.psel.common.utils.UserDataSerializer;

/**
 * @author dlago
 *
 * Clase que contiene el usuario de acceso a la aplicación
 */
public class UserData implements Serializable {
	
	/**
	 * Identificador de la serialización
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Usuario de acceso a la aplicación
	 */
	private String userName = null;
	
	/**
	 * Fecha inicio campo: F_DESDE_PRESENTACION_SOLIC
	 */
	private Date initDate = null;
	
	/**
	 * Fecha fin campo: F_HASTA_PRESENTACION_SOLIC
	 */
	private Date endDate = null;
	
	/**
	 * Fecha de cumplimiento edad mínima campo: F_CUMPLIMIENTO_EDAD_MINIMA
	 */
	private Date minAgeDate = null;
	
	/**
	 * Edad mínima campo: F_HASTA_PRESENTACION_SOLIC
	 */
	private int minAge = -1;
	
	/**
	 * Precio convocatoria campo: Q_PRECIO_CONVOCATORIA
	 */
	private double callPrice = -1;
	
	/**
	 * Número de cuenta campo: N_CUENTA_IBAN
	 */
	private String bankAccount = null;
	
	/**
	 * Entidad bancaria: D_ENTIDAD_BANCARIA
	 */
	private String bank = null;
	
	/**
	 * Destinatario campo: D_DESTINATARIO
	 */
	private String receiver = null;
	
	
	/**
     * El constructor que tenemos asociado al elemento Gson.
     */
    private static final GsonBuilder gb = new GsonBuilder().setPrettyPrinting()
                                                           .registerTypeAdapter(UserData.class, new UserDataSerializer())
                                                           .registerTypeAdapter(UserData.class, new UserDataDeserializer());
    
	/**
	 * Devuelve el nombre del usuario
	 * @return Nombre del usuario
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Establece el Nombre del usuario
	 * @param userName Nombre del usuario
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Devuelve la fecha inicio campo: F_DESDE_PRESENTACION_SOLIC
	 * @return Fecha inicio campo: F_DESDE_PRESENTACION_SOLIC
	 */
	public Date getInitDate() {
		return initDate;
	}

	/**
	 * Establece la fecha inicio campo: F_DESDE_PRESENTACION_SOLIC
	 * @param initDate Fecha inicio campo: F_DESDE_PRESENTACION_SOLIC
	 */
	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	/**
	 * Devuelve la fecha fin campo: F_HASTA_PRESENTACION_SOLIC
	 * @return Fecha fin campo: F_HASTA_PRESENTACION_SOLIC
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Establece la Fecha fin campo: F_HASTA_PRESENTACION_SOLIC
	 * @param endDate Fecha fin campo: F_HASTA_PRESENTACION_SOLIC
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Devuelve la fecha de cumplimiento edad mínima campo: F_CUMPLIMIENTO_EDAD_MINIMA
	 * @return Fecha de cumplimiento edad mínima campo: F_CUMPLIMIENTO_EDAD_MINIMA
	 */
	public Date getMinAgeDate() {
		return minAgeDate;
	}

	/**
	 * Establece la fecha de cumplimiento edad mínima campo: F_CUMPLIMIENTO_EDAD_MINIMA 
	 * @param minAgeDate Fecha de cumplimiento edad mínima campo: F_CUMPLIMIENTO_EDAD_MINIMA
	 */
	public void setMinAgeDate(Date minAgeDate) {
		this.minAgeDate = minAgeDate;
	}

	/**
	 * Devuelve la edad mínima campo: F_HASTA_PRESENTACION_SOLIC
	 * @return Edad mínima campo: F_HASTA_PRESENTACION_SOLIC
	 */
	public int getMinAge() {
		return minAge;
	}
	
	/**
	 * Establece la edad mínima campo: F_HASTA_PRESENTACION_SOLIC
	 * @param minAge Edad mínima campo: F_HASTA_PRESENTACION_SOLIC
	 */
	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	/**
	 * Devuelve el precio convocatoria campo: Q_PRECIO_CONVOCATORIA
	 * @return  Precio convocatoria campo: Q_PRECIO_CONVOCATORIA
	 */
	public double getCallPrice() {
		return callPrice;
	}

	/**
	 * Establece el precio convocatoria campo: Q_PRECIO_CONVOCATORIA
	 * @param callPrice Precio convocatoria campo: Q_PRECIO_CONVOCATORIA
	 */
	public void setCallPrice(double callPrice) {
		this.callPrice = callPrice;
	}

	/**
	 * Devuelve el número de cuenta campo: N_CUENTA_IBAN
	 * @return Número de cuenta campo: N_CUENTA_IBAN
	 */
	public String getBankAccount() {
		return bankAccount;
	}

	/**
	 * Establece el número de cuenta campo: N_CUENTA_IBAN
	 * @param bankAccount Número de cuenta campo: N_CUENTA_IBAN
	 */
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	/**
	 * Devuelve la entidad bancaria: D_ENTIDAD_BANCARIA
	 * @return Entidad bancaria: D_ENTIDAD_BANCARIA
	 */
	public String getBank() {
		return bank;
	}

	/**
	 * Establece la entidad bancaria: D_ENTIDAD_BANCARIA
	 * @param bank Entidad bancaria: D_ENTIDAD_BANCARIA
	 */
	public void setBank(String bank) {
		this.bank = bank;
	}

	/**
	 * Devuelve el destinatario convocatoria campo: D_DESTINATARIO
	 * @return Destinatario convocatoria campo: D_DESTINATARIO
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * Establece el destinatario convocatoria campo: D_DESTINATARIO
	 * @param receiver Destinatario convocatoria campo: D_DESTINATARIO
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	/**
	 * Convierte el objeto UrgencyData a JSON
	 * @return Json del objeto UrgencyData
	 */
	public String toJSON() {
		Gson g = gb.create();
		return g.toJson(this);
	}

	/**
	 * Genera un objeto UrgencyData a partir de un objeto en formato JSON
	 * @param input Json a partir del cual se generar objeto UrgencyData
	 * @return Devuelve el objeto UrgencyData
	 */
	public static final UserData fromJSON(String input) {
		Gson g = gb.create();
		return g.fromJson(input, UserData.class);
	}
}
