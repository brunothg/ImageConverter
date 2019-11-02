package propra.imageconverter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Klasse für den Zugriff auf Kommandozeilenargumente
 *
 * @author marvin
 *
 */
public class CliParameters {

	public static final String ARGUMENT_NAME_INPUT = "input";
	public static final String ARGUMENT_NAME_OUTPUT = "output";
	public static final String ARGUMENT_NAME_COMPRESSION = "compression";

	public static final String ARGUMENT_NAME_BASE32_ENCODE = "encode-base-32";
	public static final String ARGUMENT_NAME_BASE32_DECODE = "decode-base-32";
	public static final String ARGUMENT_NAME_BASEN_ENCODE = "encode-base-n";
	public static final String ARGUMENT_NAME_BASEN_DECODE = "decode-base-n";

	private static final String ARGUMENT_KEY_VALUE_DIVIDER_REGEX = "=";
	private static final String ARGUMENT_PREFIX = "--";

	private final Map<String, String> parameters = new HashMap<>();
	private final Set<String> flags = new HashSet<>();

	/**
	 * Gibt den Wert zu dem Parameter. Kann null sein.
	 *
	 * @param name Der Parametername
	 * @return Den Parameterwert oder null, wenn nicht vorhanden
	 */
	public String getParameter(final String name) {
		final String value = this.parameters.get(name);

		return value;
	}

	/**
	 * Fügt einen neuen Parameterwert hinzu oder überschreibt diesen.
	 *
	 * @param name  Der Parametername
	 * @param value Der Parameterwert
	 */
	private void setParameter(final String name, final String value) {
		this.parameters.put(name, value);
	}

	/**
	 * Gibt zurück, ob ein Flag gestzt ist
	 *
	 * @param name Flag-Name
	 * @return true, wenn die Flag gestzt ist, sonst false
	 */
	public boolean isFlag(final String name) {
		return this.flags.contains(name);
	}

	/**
	 * Setzt oder Entfernt ein Flag
	 *
	 * @param name   Flag-Name
	 * @param active wenn true wird Flag gesetzt, sonst entfernt
	 */
	private void setFlag(final String name, final boolean active) {
		if (active) {
			this.flags.add(name);
		} else {
			this.flags.remove(name);
		}
	}

	/**
	 * Gibt den Wert für den Input-Parameter
	 *
	 * @return Input-Parameter
	 */
	public String getInputFileString() {
		return this.getParameter(CliParameters.ARGUMENT_NAME_INPUT);
	}

	/**
	 * Gibt den Wert für die Kompressionsart
	 *
	 * @return Kompressionsart oder null für default
	 */
	public String getCompression() {
		return this.getParameter(CliParameters.ARGUMENT_NAME_COMPRESSION);
	}

	/**
	 * Gibt die Dateierweiterung des Input-Parameters
	 *
	 * @return Die Dateierweiterung des Input-Parameters
	 * @throws ParameterException wenn der Input-Parameter fehlerhaft ist
	 */
	public String getInputFileExtension() throws ParameterException {
		final String inputFileString = this.getInputFileString();
		if (inputFileString == null) {
			throw new ParameterException("Der Eingabeparameter (--" + CliParameters.ARGUMENT_NAME_INPUT + ") fehlt");
		}

		final int indexOfExtension = inputFileString.lastIndexOf('.');
		if ((indexOfExtension < 0) || (indexOfExtension >= (inputFileString.length() - 1))) {
			throw new ParameterException(
					"Dem Eingabeparameter (--" + CliParameters.ARGUMENT_NAME_INPUT + ") fehlt die Dateierweiterung");
		}

		return inputFileString.substring(indexOfExtension + 1);
	}

	/**
	 * Gibt den {@link Path} zum Input-File
	 *
	 * @return Input-File Path
	 * @throws ParameterException wenn der Input-Parameter fehlerhaft ist
	 */
	public Path getInputFile() throws ParameterException {
		final String inputFileString = this.getInputFileString();
		if (inputFileString == null) {
			throw new ParameterException("Der Eingabeparameter (--" + CliParameters.ARGUMENT_NAME_INPUT + ") fehlt");
		}

		return Paths.get(inputFileString);
	}

	/**
	 * Gibt den Wert für den Input-Parameter
	 *
	 * @return Input-Parameter
	 */
	public String getOutputFileString() {
		return this.getParameter(CliParameters.ARGUMENT_NAME_OUTPUT);
	}

	/**
	 * Gibt die Dateierweiterung des Output-Parameters
	 *
	 * @return Die Dateierweiterung des Output-Parameters
	 * @throws ParameterException wenn der Output-Parameter fehlerhaft ist
	 */
	public String getOutputFileExtension() throws ParameterException {
		final String outputFileString = this.getOutputFileString();
		if (outputFileString == null) {
			throw new ParameterException("Der Ausgabeparameter (--" + CliParameters.ARGUMENT_NAME_OUTPUT + ") fehlt");
		}

		final int indexOfExtension = outputFileString.lastIndexOf('.');
		if ((indexOfExtension < 0) || (indexOfExtension >= (outputFileString.length() - 1))) {
			throw new ParameterException(
					"Dem Ausgabeparameter (--" + CliParameters.ARGUMENT_NAME_OUTPUT + ") fehlt die Dateierweiterung");
		}

		return outputFileString.substring(indexOfExtension + 1);
	}

	/**
	 * Gibt den {@link Path} zum Output-File
	 *
	 * @return Output-File Path
	 * @throws ParameterException wenn der Output-Parameter fehlerhaft ist
	 */
	public Path getOutputFile() throws ParameterException {
		final String outputFileString = this.getOutputFileString();
		if (outputFileString == null) {
			throw new ParameterException("Der Ausgabeparameter (--" + CliParameters.ARGUMENT_NAME_OUTPUT + ") fehlt");
		}

		return Paths.get(outputFileString);
	}

	/**
	 * Gibt an, ob das Base32-Encode flag gesetzt ist
	 *
	 * @return true oder false
	 */
	public boolean isBase32Encode() {
		return this.isFlag(CliParameters.ARGUMENT_NAME_BASE32_ENCODE);
	}

	/**
	 * Gibt an, ob das Base32-Decode flag gesetzt ist
	 *
	 * @return true oder false
	 */
	public boolean isBase32Decode() {
		return this.isFlag(CliParameters.ARGUMENT_NAME_BASE32_DECODE);
	}

	/**
	 * Gibt an, ob das Base-N-Decode flag gesetzt ist
	 *
	 * @return true oder false
	 */
	public boolean isBaseNDecode() {
		return this.isFlag(CliParameters.ARGUMENT_NAME_BASEN_DECODE);
	}

	/**
	 * Gibt an, ob das Base-N-Encode Alphabet
	 *
	 * @return true oder false
	 */
	public char[] getBaseNEncodeAlphabet() {
		final String parameter = this.getParameter(CliParameters.ARGUMENT_NAME_BASEN_ENCODE);
		if (parameter == null) {
			return null;
		}

		return parameter.toCharArray();
	}

	/**
	 * Liest die übergebenen Kommandozeilenargumente ein. Erwartet werden Argumente
	 * der Form: <code>--name=wert</code> Wichtig ist, dass dies EIN Argument ist.
	 * Argumente mit Leerzeichen müssen in der Shell z.B. in " gefasst werden.
	 *
	 * @param arguments Die Kommandozeilenargumente
	 */
	public void parse(final String[] arguments) {
		for (final String argument : arguments) {
			this.parseArgument(argument);
		}
	}

	/**
	 * Liest ein einzelnes Argument
	 *
	 * @see #parse(String[])
	 * @param argument Das Argument
	 */
	private void parseArgument(final String argument) {
		if (argument.startsWith(CliParameters.ARGUMENT_PREFIX)) {
			final String[] keyAndValue = argument.substring(CliParameters.ARGUMENT_PREFIX.length())
					.split(CliParameters.ARGUMENT_KEY_VALUE_DIVIDER_REGEX, 2);

			// Guard für ungültige Parameter
			if (keyAndValue.length == 2) {
				final String key = keyAndValue[0];
				final String value = keyAndValue[1];
				this.setParameter(key, value);
			} else if (keyAndValue.length == 1) {
				final String key = keyAndValue[0];
				this.setFlag(key, true);
			}

		}
	}

	@Override
	public String toString() {
		return "CliParameters [parameters=" + this.parameters + "]";
	}
}
