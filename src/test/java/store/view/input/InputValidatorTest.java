package store.view.input;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class InputValidatorTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("아무 입력도 하지 않은 경우 예외가 발생한다.")
    void validateNotNullOrEmptyTest(String input) {
        //when & then
        assertThatThrownBy(() -> InputValidator.validateNotNullOrEmpty(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(InputErrorMessage.INVALID_INPUT.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"사이다-3, 콜라-2", "(사이다-3), (콜라-2)", "사이다:3", "[사이다-3, 콜라-3, 탄산수-1]"})
    @DisplayName("잘못된 형식으로 입력한 경우 예외가 발생한다.")
    void validateFormatTest(String input) {
        //when & then
        assertThatThrownBy(() -> InputValidator.validateFormat(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(InputErrorMessage.INCORRECT_INPUT_FORMAT.getMessage());
    }
}