package org.example.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandProcessorTest {

    private static final String INVALID_COMMAND_FORMAT_EXCEPTION = "Invalid command format. Expected: Command (args)";
    @Mock
    private Command mockCommand1;

    @Mock
    private Command mockCommand2;

    private CommandProcessor commandProcessor;

    @BeforeEach
    void setUp() {
        when(mockCommand1.getName()).thenReturn("TestCommand1");
        when(mockCommand2.getName()).thenReturn("TestCommand2");

        commandProcessor = new CommandProcessor(List.of(mockCommand1, mockCommand2));
    }

    @ParameterizedTest
    @MethodSource("validCommandProvider")
    void process_shouldHandleValidCommands(String input, String expectedCommand, String[] expectedArgs) {

        when(mockCommand1.execute(any())).thenReturn("Success");

        String result = commandProcessor.process(input);

        verify(mockCommand1).execute(expectedArgs);
        assertEquals("Success", result);
    }

    private static Stream<Object[]> validCommandProvider() {
        return Stream.of(
                new Object[]{"TestCommand1()", "TestCommand1", new String[0]},
                new Object[]{"TestCommand1(  )", "TestCommand1", new String[0]},
                new Object[]{"TestCommand1(arg1)", "TestCommand1", new String[]{"arg1"}},
                new Object[]{"TestCommand1(arg1, arg2)", "TestCommand1", new String[]{"arg1", "arg2"}},
                new Object[]{"TestCommand1( arg1 , arg2 )", "TestCommand1", new String[]{"arg1", "arg2"}}
        );
    }

    @ParameterizedTest
    @MethodSource("invalidFormatProvider")
    void parseInput_shouldThrowOnInvalidFormats(String input, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandProcessor.process(input)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Object[]> invalidFormatProvider() {
        return Stream.of(
                new Object[]{"", "Empty input"},
                new Object[]{"TestCommand1", INVALID_COMMAND_FORMAT_EXCEPTION},
                new Object[]{"TestCommand1(arg1, arg2", INVALID_COMMAND_FORMAT_EXCEPTION},
                new Object[]{"TestCommand1 arg1, arg2)", INVALID_COMMAND_FORMAT_EXCEPTION},
                new Object[]{"TestCommand1[arg1, arg2]", INVALID_COMMAND_FORMAT_EXCEPTION}
        );
    }

}