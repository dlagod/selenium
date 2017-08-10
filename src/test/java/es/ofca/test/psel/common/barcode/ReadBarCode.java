package es.ofca.test.psel.common.barcode;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/**
 * Clase que permite leer un código de barras
 * 
 * @author dlago
 */
public class ReadBarCode {
		
	/**
	 * Método que decodifica un código de barras en formato PDF417
	 * @param imageBarCode Imagen con el código de barras
	 * @return Código de barras
	 * @throws Exception Excepción producida
	 */
	public static String decode(String imageBarCode) throws Exception {
		
		InputStream barCodeInputStream = new FileInputStream(imageBarCode);  
		BufferedImage barCodeBufferedImage = ImageIO.read(barCodeInputStream);  
		  
		LuminanceSource source = new BufferedImageLuminanceSource(barCodeBufferedImage);  
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));  
		Reader reader = new MultiFormatReader();  
		Result result = reader.decode(bitmap);
		
		return result.getText();
	}
	
	
	/**
	 * Main method.
	 * 
	 * @param args no arguments needed
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		
		/** Bar Code Image */
		String IMAGE = "barcode_1734_83971119M1470900349696.jpg";
		//String tmpDir = System.getProperty("java.io.tmpdir");
		//String IMAGE = tmpDir + "barcode_1734_83971119M1470900349696.png";

		String barCode = ReadBarCode.decode(IMAGE);
		System.out.println("Barcode text is: " + barCode);
	}

}
