package es.ofca.test.psel.common.constant;

public final class Constants {
	
	public enum Methods {
		CLEAR("clear"), 
		FILL("fill"),  
		FILL_REQUIRED("fill"), 
		LOAD_DRIVER("loadDriver"), 
		SET_MAP_VALUES("setMapValues"), 
		CHECK_VALUE("checkValue"), 
		LOAD_ACCESS_TYPE_CALL("loadAccessTypeCall"), 
		LOAD_REQUIRE_DUPLICATE_CALL("loadRequireDuplicateCall"), 
		SET_DNI("setDni"), 
		SET_NIE("setNie");

	    private final String value;

	    private Methods(final String value) {
	        this.value = value;
	    }

	    public String getValue(){
	        return value;
	    }
	}
	
	public enum Browser {
		FIREFOX("firefox"), 
		IE("internet explorer"),  
		CHROME("chrome");

	    private final String value;

	    private Browser(final String value) {
	        this.value = value;
	    }

	    public String getValue(){
	        return value;
	    }
	}
	
	public static final String UTF_8 = "UTF-8";
	public static final String JPG = ".jpg";
	public static final String PNG = "png";
	public static final String PDF = ".pdf";
	public static final String CADENA_VACIA = "";
	public static final String BLANK_SPACE = " ";
	public static final String REQUIRED_VALUE = "(*)";
	public static final String NO = "No";
	public static final String FALSE = "false";
	public static final String SI = "Sí";
	public static final String TRUE = "true";
	public static final String ARIA_CHECKED = "aria-checked";
	public static final String VALUE = "value";
	
	public static final String REMOTE_WEB_HOST = "remoteWebHost";
	public static final String REMOTE_DIR = "remoteDir";
	public static final String BASE_URL = "baseUrl";
	public static final String TEST_RESOURCES_DIRECTORY = "testResourcesDirectory";
	
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public static final String DATE_MIN_FORMAT = "dd/MM/yyyy";
	
	public static final int TIME_OUT_SECONDS = 30;
	public static final int TIME_OUT_PDF_SECONDS = 60;
	public static final int RETRY = 500;
	
	public static final String DOWNLOAD_AUTOIT = "autoit\\Download.exe";
}
