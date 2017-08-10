package es.ofca.test.psel.common.pdf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.util.PDFTextStripper;

import es.ofca.test.psel.common.constant.Constants;


/**
 * Clase que permite leer un PDF (PDFbox-1.8.11)
 * 
 * @author dlago
 */
public class ReadContentPDF {
	
	private static final Logger LOGGER = Logger.getLogger(ReadContentPDF.class);

	/**
	 * Devuelve el texto del fichero PDF
	 * @param pdfFile Ruta abosulta del fichero PDF
	 * @return Devuelve el texto del fichero PDF
	 * @throws IOException Excepción producida
	 */
	public static String getText(String pdfFile) throws IOException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ReadContentPDF.getText");
		}
		
		File file = new File(pdfFile);
		PDFTextStripper stripper = new PDFTextStripper();
		PDDocument doc = PDDocument.load(file);
		return stripper.getText(doc);
	}
	
	
	/**
	 * Guarda las imagenes del fichero PDF
	 * @param pdfFile Ruta abosulta del fichero PDF
	 * @throws IOException Excepción producida
	 */
	public static void readImages(String pdfFile) throws IOException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ReadContentPDF.readImages");
		}
		PDDocument pdfDocument = null;
		try {
			pdfDocument = PDDocument.load(pdfFile);
			List<PDPage> list = pdfDocument.getDocumentCatalog().getAllPages();
			if ((list != null) && (!list.isEmpty())) {
				PDResources pdResources = list.get(0).getResources();
				Map<String, PDXObjectImage> pageImages = pdResources.getImages();
			    if (pageImages != null) {
			    	int totalImages = 1;
			        Iterator imageIter = pageImages.keySet().iterator();
			        while (imageIter.hasNext()) {
			            String key = (String) imageIter.next();
			            PDXObjectImage pdxObjectImage = (PDXObjectImage) pageImages.get(key);
			            //PDF417
			            pdxObjectImage.write2file("barcode_" + totalImages);
			            totalImages++;
			        }
			    }
			}
		} finally {
			// Se cierra el documento
			if (pdfDocument != null) {
				pdfDocument.close();
			}
		}
	}
	
	
	/**
	 * Devuelve la ruta imagen del fichero PDF
	 * @param pdfFile Ruta abosulta del fichero PDF
	 * @return Devuelve el path de la imagen
	 * @throws IOException Excepción producida
	 */
	public static String readImage(String pdfFile) throws IOException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ReadContentPDF.readImage");
		}
		
		// Directorio temporal de escritura
		File pdf = new File(pdfFile);
		String pdfName = pdf.getName();
		String fileName = "barcode_" + pdfName.substring(0, pdfName.lastIndexOf(Constants.PDF));
		String pathFileName = pdf.getParent() + File.separator + fileName;
		
		PDDocument pdfDocument = null;
		
		try {
			pdfDocument = PDDocument.load(pdfFile);
			List<PDPage> list = pdfDocument.getDocumentCatalog().getAllPages();
			if ((list != null) && (!list.isEmpty())) {
				PDResources pdResources = list.get(0).getResources();
				Map<String, PDXObjectImage> pageImages = pdResources.getImages();
			    if (pageImages != null) {
			        Iterator imageIter = pageImages.keySet().iterator();
			        while (imageIter.hasNext()) {
			            String key = (String) imageIter.next();
			            //PDF417
			            PDXObjectImage pdxObjectImage = (PDXObjectImage) pageImages.get(key);
			            pdxObjectImage.write2file(pathFileName);
			
			           break;
			        }
			    }
			}
		} finally {
			// Se cierra el documento
			if (pdfDocument != null) {
				pdfDocument.close();
			}
		}
		
		return pathFileName + Constants.JPG;
	}
	
	/**
	 * Pinta los campos del fichero PDF
	 * @param pdfFile Ruta abosulta del fichero PDF
	 * @throws IOException Excepción producida
	 */
	public static Map<String,String> printFields(String pdfFile) throws IOException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("ReadContentPDF.printFields");
		}
		
		//Mapa de campos que contiene el PDF
		Map<String,String> valuesMap = null;
		PDDocument pdfDocument = null;
		try {
			pdfDocument = PDDocument.load(pdfFile);
			PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			List fields = acroForm.getFields();
			Iterator fieldsIter = fields.iterator();
 
			System.out.println(new Integer(fields.size()).toString() + " top-level fields were found on the form");
			
			valuesMap = new HashMap<String, String>();
 
			while( fieldsIter.hasNext()) {
			    PDField field = (PDField)fieldsIter.next();
			    processField(field, "|--", field.getPartialName(), valuesMap);
			}
			
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("ReadContentPDF.printFields ---> Values PDF: " + valuesMap.toString());
			}
		} finally {
			// Se cierra el documento
			if (pdfDocument != null) {
				pdfDocument.close();
			}
		}
		        
        return valuesMap;
    }
    
	/**
	 * Procesamiento de los campos del Fichero
	 * @param field Campo
	 * @param sLevel Nivel en la Jerarquía
	 * @param sParent Padre
	 * @param valuesMap Mapa de campos del PDF
	 * @throws IOException Excepción producida
	 */
	private static void processField(PDField field, String sLevel, String sParent, Map<String,String> valuesMap) throws IOException {
		List kids = field.getKids();
		if (kids != null) {
			Iterator kidsIter = kids.iterator();
			if (!sParent.equals(field.getPartialName())) {
				sParent = sParent + "." + field.getPartialName();
			}

			System.out.println(sLevel + sParent);

			while (kidsIter.hasNext()) {
				Object pdfObj = kidsIter.next();
				if (pdfObj instanceof PDField) {
					PDField kid = (PDField) pdfObj;
					processField(kid, "|  " + sLevel, sParent, valuesMap);
				}
			}
		} else {
			String outputString = sLevel + sParent + "." + field.getPartialName() + ",  type="
					+ field.getClass().getName() + ", value=" + field.getValue();
			valuesMap.put(sParent + "." + field.getPartialName(), field.getValue());
			System.out.println(outputString);
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args no arguments needed
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		/** Text file containing information about a PDF file. */
		String PDF = "2609_14252355Z1491305059216.pdf";

		// Lectura del PDF
		Map<String,String> valuesMap = ReadContentPDF.printFields(PDF);
		
		
		//Pintado del Mapa de Valores
		System.out.println();
		System.out.println("************************* MAPA DE VALORES **************************");
		if ((valuesMap != null) && (!valuesMap.isEmpty())) {
			Iterator<String> keySet = valuesMap.keySet().iterator();
			while (keySet.hasNext()) {
				String key = (String) keySet.next();
				String value = (String) valuesMap.get(key);
				System.out.println(key + ":" + value);
			}
		}
		
		//Lectura de las imagenes
		ReadContentPDF.readImages(PDF);
	}

}
