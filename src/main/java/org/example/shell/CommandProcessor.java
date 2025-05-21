package org.example.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandProcessor {

    private static final String UNKNOWN_COMMAND_EXCEPTION = "Unknown command. Available commands: ";
    private static final String INVALID_FORMAT_EXCEPTION = "Invalid command format. Expected: Command (args)";
    private final Map<String, Command> commands = new HashMap<>();

    @Autowired
    public CommandProcessor(List<Command> commandList) {
        this.commands.putAll(commandList.stream()
                .collect(Collectors.toMap(
                        Command::getName,
                        Function.identity()
                )));
    }
    
    public String process(String input) {
        ParsedCommand parsedCommand = parseInput(input);
        
        Command command = commands.get(parsedCommand.commandName);
        if (command == null) {
            return UNKNOWN_COMMAND_EXCEPTION + commands.keySet();
        }
        
        return command.execute(parsedCommand.arguments());
    }

    private ParsedCommand parseInput(String input) {
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Empty input");
        }

        Pattern pattern = Pattern.compile("^(\\w+)\\s*\\(([^)]*)\\)$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(INVALID_FORMAT_EXCEPTION);
        }

        String commandName = matcher.group(1);
        String argsString = matcher.group(2).trim();
        String[] arguments = argsString.isEmpty() ? new String[0] : argsString.split("\\s*,\\s*");

        return new ParsedCommand(commandName, arguments);
    }

    private record ParsedCommand(String commandName, String[] arguments) {}
}