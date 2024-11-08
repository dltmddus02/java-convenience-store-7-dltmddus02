package store.view.input;

import java.util.regex.Pattern;
import store.view.input.exception.InputErrorMessage;
import store.view.input.exception.InputException;

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
        if (!isCorrectFormat(input)) {
            throw new InputException(InputErrorMessage.INCORRECT_INPUT_FORMAT);
        }
    }

    private static boolean isCorrectFormat(String input) {
        String productPattern = "[ㄱ-ㅎ가-힣a-zA-Z]+";
        String numberPattern = "[0-9]+";

        String singlePattern = String.format("\\[(%s)-(%s)]", productPattern, numberPattern);
        String repeatPattern = String.format("%s(,%s)*", singlePattern, singlePattern);

        Pattern correctPattern = Pattern.compile("^" + repeatPattern + "$");

        return correctPattern.matcher(input).find();
    }
}
