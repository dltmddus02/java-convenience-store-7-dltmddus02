package store.view.input;

public class InputException extends IllegalArgumentException{

    public InputException(InputErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
