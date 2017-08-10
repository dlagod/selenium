package es.ofca.test.psel.common.beans;

/**
 * Clase que contiene los campos del formulario
 * @author dlago
 */
public class ValueForm {
	
	private String name = null;
	private String value = null;
	private String propertySummary = null;
	private String propertyDetail = null;
	private String fieldPDF = null;
	private boolean required = false;
	
	
	
	/**
	 * Constructor con parámetros
	 * @param name Nombre del campo
	 * @param value Valor del campo
	 * @param propertySummary  Nombre del campo en el fichero de propiedades.
	 * @param propertyDetail  Valor del fichero de propiedades.
	 * @param fieldPDF Mapeo del campo con el PDF
	 * @param required Parámetro booleano para indicar si es requerido
	 */
	public ValueForm(String name, String value, String propertySummary, String propertyDetail, String fieldPDF, boolean required) {
		this.name = name;
		this.value = value;
		this.propertySummary = propertySummary;
		this.propertyDetail = propertyDetail;
		this.fieldPDF = fieldPDF;
		this.required = required;
	}

	/**
	 * Devuelve el nombre del campo
	 * @return Devuelve el nombre del campo
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Establece el nombre del campo
	 * @param name El nombre del campo
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Se devuelve el valor del campo
	 * @return Valor del campo
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Establece el valor del campo
	 * @param value Valor del campo
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Devuelve el valor del fichero de propiedades para el campo summary
	 * @return Valor del fichero de propiedades para el campo summary
	 */
	public String getPropertySummary() {
		return propertySummary;
	}

	/**
	 * Establece el valor del fichero de propiedades para el campo summary
	 * @param propertySummary Valor del fichero de propiedades para el campo summary
	 */
	public void setPropertySummary(String propertySummary) {
		this.propertySummary = propertySummary;
	}

	/**
	 * Devuelve la correlación entre el campo y el fichero de propiedades
	 * @return Correlación entre el campo y el fichero de propiedades
	 */
	public String getPropertyDetail() {
		return propertyDetail;
	}
	
	/**
	 * Establece la correlación entre el campo y el fichero de propiedades.
	 * @param property key del fichero de propiedades.
	 */
	public void setPropertyDetail(String propertyDetail) {
		this.propertyDetail = propertyDetail;
	}
	
	/**
	 * Devuelve el campo que hace referencia en el PDF	
	 * @return Campo que hace referencia en el PDF	
	 */
	public String getFieldPDF() {
		return fieldPDF;
	}

	/**
	 * Establece el campo que hace referencia en el PDF
	 * @param fieldPDF Campo que hace referencia en el PDF
	 */
	public void setFieldPDF(String fieldPDF) {
		this.fieldPDF = fieldPDF;
	}

	/**
	 * Método que indica si es un campo o no requerido
	 * @return true/false dependiendo si es un campo requerido.
	 */
	public boolean isRequired() {
		return required;
	}
	
	/**
	 * Establece si el campo es o no requerido
	 * @param required Parámetro booleano para indicar si es requerido
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
}
