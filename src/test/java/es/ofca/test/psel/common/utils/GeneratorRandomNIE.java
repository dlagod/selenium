package es.ofca.test.psel.common.utils;


public class GeneratorRandomNIE {
	
	public static final int DEFAULT_LIMIT = 10;
	public static final String DEFAULT_FILE_NAME = "NIE.csv";
	
	private static final int DIVISOR = 23;
	private static final char LETRAS[] = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
			'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	
	private static final char LETRA_INICIAL[] = { 'X', 'Y', 'Z' };
	
	/**
	 * Método que permite la generación aleatoria de un NIE
	 * @return NIE aleatorio
	 */
	public static String generateNIE() {

		// Generamos un dígito de la letra inicial XYZ
		//int numeroLetraIni = randomGenerator.nextInt(LETRA_INICIAL.length);
		int numeroLetraIni = (int)(Math.random()*3 + 0);
		char letraIni = LETRA_INICIAL[numeroLetraIni];
		
		// Generamos un número de 7 dígitos
		int numero7 = ((int) Math.floor(Math.random()
				* (10000000 - 1000000) + 1000000));
		
		// Concatenar numeroLetraIni con los 7 dígitos
		Integer numeroNIE = Integer.parseInt(Integer.toString(numeroLetraIni) + Integer.toString(numero7));

		// Calcular el resto de la división		
		int resultado = numeroNIE - (numeroNIE / DIVISOR * DIVISOR);
				
		// Calculamos la letra final del NIE
		char letraNIE = generateLetterNIE(resultado);

		// Pasamos el NIE a String
		return letraIni + Integer.toString(numero7) + letraNIE;
	}

	private static char generateLetterNIE(int resultado) {
		return LETRAS[resultado];
	}
}