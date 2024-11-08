package store.view.input.exception;

public class InputException extends IllegalArgumentException {

    public InputException(InputErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
