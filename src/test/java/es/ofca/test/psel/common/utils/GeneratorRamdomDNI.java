package es.ofca.test.psel.common.utils;

public class GeneratorRamdomDNI {
	
	public static final int DEFAULT_LIMIT = 10;
	public static final String DEFAULT_FILE_NAME = "DNI.csv";
	
	private static final int DIVISOR = 23;
	private static final char LETRAS[] = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
			'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };

	
	/**
	 * Método que permite la generación aleatoria de un DNI
	 * @return DNI aleatorio
	 */
	public static String generateDNI() {

		// Generamos un número de 8 dígitos
		int numeroDNI = ((int) Math.floor(Math.random()
				* (100000000 - 10000000) + 10000000));
		int resultado = numeroDNI - (numeroDNI / DIVISOR * DIVISOR);

		// Calculamos la letra del DNI
		char letraDNI = generateLetterDNI(resultado);

		// Pasamos el DNI a String
		return Integer.toString(numeroDNI) + letraDNI;
	}

	private static char generateLetterDNI(int resultado) {
		return LETRAS[resultado];
	}
}
