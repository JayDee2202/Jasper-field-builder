package jasperfieldbuilder.util;

import java.util.Arrays;
import java.util.List;

public class ProcessConstants {
	public static final String PROGRAM_TITLE = "Vollautomagische JRXml-Verarbeitung - v1.2";
	
	public static final String ACCESS_UNDERLINED = "<html><body><u>Zugriff</u></body></html>";
	public static final String CONVENTIONS_UNDERLINED = "<html><body><u>Konventionen</u></body></html>";
	public static final String COMMENT_FIELDS = "// Felder";
	public static final String COMMENT_NOT_FOUND = "// Es wurden keine Parameter oder Felder gefunden";
	public static final String COMMENT_PARAMETER = "// Parameter";
	public static final String COPY_TO_CLIPBOARD = "In Zwischenablage einfg.";
	public static final String LINE_SEPARATOR = "\n";
	public static final String LOAD_FILE = "Datei laden";
	public static final String LOAD_INSTRUCTIONS = "<html><br>JRXml in Textfeld kopieren oder<br>JRXml per Drag'n'Drop in das Textfeld ziehen<br>oder<br>JRXml als Datei laden,<br>verarbeiten,<br>fertig!</html>";
	public static final String NO_FILE_CHOOSEN = "<html><i>Keine Datei ausgewählt</i></html>";
	public static final String PREFIX_CONSTANT_STRING = "final String";
	public static final String PREFIX_PRIVATE = "private";
	public static final String PREFIX_PROTECTED = "protected";
	public static final String PREFIX_STATIC = "static";
	public static final String PROCESS = "Verarbeiten";
	public static final String USE_FILE = "Datei verwenden";
	public static final String INFORMATION = "Information";
	public static final String ERROR_NO_FILE = "Es wurde keine Datei geladen.";
	public static final String ERROR_NO_TEXT = "Es wurde kein Text zum Verarbeiten eingegeben.";
	public static final String ERROR_PROCESSING = "Fehler beim Verarbeiten";
	
	protected static final List<String> jasperBlacklist = Arrays.asList("REPORT_CONTEXT", "REPORT_PARAMETERS_MAP", "JASPER_REPORT", "REPORT_CONNECTION", "REPORT_FORMAT_FACTORY", "REPORT_CLASS_LOADER",
			"REPORT_URL_HANDLER_FACTORY", "REPORT_FILE_RESOLVER", "REPORT_TEMPLATES", "REPORT_MAX_COUNT", "REPORT_DATA_SOURCE", "REPORT_SCRIPTLET", "REPORT_LOCALE", "REPORT_RESOURCE_BUNDLE",
			"REPORT_TIME_ZONE", "SORT_FIELDS", "FILTER", "REPORT_VIRTUALIZER", "IS_IGNORE_PAGINATION");

	private ProcessConstants() {}
}
