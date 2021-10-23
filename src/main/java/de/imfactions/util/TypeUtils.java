package de.imfactions.util;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeUtils {

    public static int toInt(Object object) {
        return Integer.parseInt(Objects.toString(object));
    }

    public static <T> T convert(Class<T> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return (T) editor.getValue();
    }

    public static boolean isInt(String object) {
        try {
            Integer.valueOf(object);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String object) {
        try {
            Double.valueOf(object);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String object) {
        try {
            Long.valueOf(object);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShort(String object) {
        try {
            Short.valueOf(object);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFloat(String object) {
        try {
            Float.valueOf(object);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static <E extends Enum<E>> boolean isEnumValue(Class<E> enumClass, String value) {
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static final Pattern unicode = Pattern.compile("(\\\\u\\p{XDigit}{4})");

    /**
     * Konvertiert einen String mit ausgeschriebenen Unicode-Escapes in einen String mit den entsprechenden Zeichen.
     * Nützlich beim Auslesen von Strings aus Dateien in denen die gewünschten Zeichen nicht erlaubt sind.
     * Beispiele:
     * <ul>
     * <li>"Stra\u00DFe" -> "Straße"</li>
     * <li>"Gr\u00FCn" -> "Grün"</li>
     * </ul>
     *
     * @param input Inputstring
     * @return Konvertierter String
     */
    public static String convertUnicode(String input) {
        Matcher match = unicode.matcher(input);
        StringBuilder output = new StringBuilder();
        int index = 0;
        while (match.find()) {
            output.append(input, index, match.start());
            output.append((char) Integer.parseInt(match.group().substring(2), 16));
            index = match.end();
        }
        output.append(input.substring(index));
        return output.toString();
    }

    public static String[] appendArrays(String[] first, String[] second) {
        String[] finalArray = new String[first.length + second.length];
        System.arraycopy(first, 0, finalArray, 0, first.length);
        System.arraycopy(second, 0, finalArray, first.length, second.length);

        return finalArray;
    }

}

