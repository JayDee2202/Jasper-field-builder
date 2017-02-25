package jasperfieldbuilder.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.omg.CORBA.portable.ApplicationException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;

/**
 *	All utils for formatting the input.  
 */
public class FormattingUtils {
	private static final String LINE_SEPARATOR = "\n";
	
	private Visibility visibility;
	private Conventions conventions;
	private CopyPaste copy;
	
	
	public FormattingUtils(Visibility visibility, Conventions conventions, CopyPaste copy) {
		this.visibility = visibility;
		this.conventions = conventions;
		this.copy = copy;
	}

	/**
	 * Processes the <code>parameterList</code> and <code>fieldList</code> using the JasperDesign.<br>
	 * The parameters will be sort out through a blacklist and given back to the formatting method, the fields are directly transfered to the formatting method.<br>
	 * After formatting the complete string will be returned.
	 * 
	 * @param parameterList JasperDesign list of parameters
	 * @param fieldList JasperDesign list of fields
	 * @return formatted list of parameters and fields
	 * @throws IOException 
	 */
	public String processJasperDesign(List<JRParameter> parameterList, List<JRField> fieldList) throws IOException {
		StringBuilder fields = new StringBuilder();
		StringBuilder parameters = new StringBuilder();

		for (int i = 0; i < parameterList.size(); ++i) {
			if (!ProcessConstants.jasperBlacklist.contains(parameterList.get(i).getName())) {
				parameters.append(parameterList.get(i).getName()).append(LINE_SEPARATOR);
			}
		}
		for (int i = 0; i < fieldList.size(); ++i) {
			fields.append(fieldList.get(i).getName()).append(LINE_SEPARATOR);
		}

		return formatForTextfield(processType(parameters.toString(), InputType.PARAMETER, false), processType(fields.toString(), InputType.PARAMETER, false));
	}

	/**
	 * Processes the input with the raw string.
	 * Using RegEx it will be verified if it's a field or a parameter and will be sorted correctly. This will handed to the formatting.<br>
	 * After formatting the complete string will be returned.
	 * 
	 * @param text raw xml-text
	 * @return formatted list of all parameters and fields
	 * @throws IOException 
	 */
	public String processRawXml(String text) throws IOException {
		List<String> input = Arrays.asList(text.split("\\r?\\n"));
		StringBuilder fields = new StringBuilder();
		StringBuilder parameters = new StringBuilder();
		String currentInput;

		for (int i = 0; i < input.size(); ++i) {
			currentInput = input.get(i).trim();
			if (currentInput.matches("(<" + InputType.PARAMETER.getDescription() + "[^>]*>)")) {
				parameters.append(currentInput).append(LINE_SEPARATOR);
			} else if (currentInput.matches("(<" + InputType.FIELD.getDescription() + "[^>]*>)")) {
				fields.append(currentInput).append(LINE_SEPARATOR);
			}
		}

		return formatForTextfield(processType(parameters.toString(), InputType.PARAMETER, true), processType(fields.toString(), InputType.FIELD, true));
	}

	/**
	 * The real processing and formatting of the input.<br>
	 * The handed text will be processed using <code>type</code>. If
	 * <code>cleanInput</code> is <code>true</code> the whole text is cleaned up
	 * by RegEx expressions except for the actual names. If the names are
	 * directly provided, for example via file, <code>cleanInput</code> must be
	 * <code>false</code>. <br>
	 * Following the constant names will be build and the reprocessed constants
	 * will be returned line separated.
	 * 
	 * @param text
	 *            text input, could be the parameter-/fieldnames or the raw
	 *            xml-code
	 * @param type
	 *            type of the handed files, <code>parameter</code> or
	 *            <code>field</code>
	 * @param cleanInput
	 *            <code>true</code> if a cleanup is needed (for raw xml-code),
	 *            <code>false</code> if the fieldnames are directly handed by
	 * @return Constants with line breaks as String
	 * @throws IOException
	 * @throws ApplicationException
	 */
	private String processType(String text, InputType type, boolean cleanInput) throws IOException {
		if (text == null || type == null) {
			throw new IllegalArgumentException("The processing method was called wrong");
		}

		if (type != InputType.PARAMETER && type != InputType.FIELD) {
			throw new IllegalArgumentException("A not defined type was passed by");
		}

		List<String> strings = Arrays.asList(text.split("\\r?\\n"));
		StringBuilder sb = new StringBuilder();
		String regExReplace = "(<" + type.getDescription() + " name=\")|(\" class=\".*\"[^>]*>)";
		String jasperVarName;

		for (int i = 0; i < strings.size(); ++i) {
			if (cleanInput) {
				jasperVarName = strings.get(i).replaceAll(regExReplace, "").trim();
			} else {
				jasperVarName = strings.get(i).trim();
			}

			if (jasperVarName.length() > 0) {
				char[] charArray = jasperVarName.toCharArray();
				StringBuilder javaVarName = new StringBuilder();
				if (conventions == Conventions.STATIC) {
					for (int j = 0; j < charArray.length; ++j) {
						if (Character.isUpperCase(charArray[j])) {
							javaVarName.append("_");
						}
						javaVarName.append(Character.toUpperCase(charArray[j]));
					}
				} else {
					javaVarName.append(jasperVarName);
				}
				
				switch (visibility) {
					case PRIVATE:
						sb.append(ProcessConstants.PREFIX_PRIVATE).append(" ");
						break;
					case PUBLIC:
						sb.append(ProcessConstants.PREFIX_PROTECTED).append(" ");
						break;
					default:
						throw new IOException("A not defined access modifier was called");
				}
				

				if (conventions == Conventions.STATIC) {
					sb.append(ProcessConstants.PREFIX_STATIC).append(" ");
				}
				sb.append(ProcessConstants.PREFIX_CONSTANT_STRING).append(" ").append(javaVarName).append(" = \"").append(jasperVarName).append("\";").append(LINE_SEPARATOR);
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * Builds the formatting and the output for the textfield.<br>
	 * Sets the output into the clipboard, if needed.
	 * 
	 * @param parameters for output prepared parameters 
	 * @param fields for output prepared fields
	 * @return string for the textfield
	 */
	private String formatForTextfield(String parameters, String fields) {
		StringBuilder result = new StringBuilder();
		boolean thereIsSomeContent = false;

		if (parameters.length() > 1) {
			thereIsSomeContent = true;
			result.append(ProcessConstants.COMMENT_PARAMETER);
			result.append(ProcessConstants.LINE_SEPARATOR);
			result.append(parameters);
			result.append(ProcessConstants.LINE_SEPARATOR);
		}

		if (fields.length() > 1) {
			thereIsSomeContent = true;
			result.append(ProcessConstants.COMMENT_FIELDS);
			result.append(ProcessConstants.LINE_SEPARATOR);
			result.append(fields);
		}

		if (!thereIsSomeContent) {
			result.append(ProcessConstants.COMMENT_NOT_FOUND);
		}


		if (copy == CopyPaste.COPY && thereIsSomeContent) {
			Clipboard zal = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection text = new StringSelection(result.toString());
			zal.setContents(text, null);
		}

		return result.toString();
	}
	
}
