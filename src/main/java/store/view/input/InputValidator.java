package store.view.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
    public static void validate(String input) {
        validateNotNullOrEmpty(input);
        validateFormat(input);
    }

    public static void validateNotNullOrEmpty(String input) {
        if (input == null || input.isBlank()) {
            throw new InputException(InputErrorMessage.INVALID_INPUT);
        }
    }

    public static void validateFormat(String input) {
        Pattern pattern = Pattern.compile("\\[([ㄱ-ㅎ가-힣a-zA-Z]+)-([0-9]+)]");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new InputException(InputErrorMessage.INCORRECT_INPUT_FORMAT);
        }
    }
}
