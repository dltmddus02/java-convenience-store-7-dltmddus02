package store.view.input;

import camp.nextstep.edu.missionutils.Console;

public class InputView {
    public static String inputProductNameAndQuantity() {
        String ProductNameAndQuantity = Console.readLine();
        InputValidator.validate(ProductNameAndQuantity);
        return ProductNameAndQuantity;
    }
}
