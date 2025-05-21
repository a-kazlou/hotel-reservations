package org.example.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandProcessor {

    private static final String UNKNOWN_COMMAND_EXCEPTION = "Unknown command. Available commands: ";
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
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0];
        String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];
        
        Command command = commands.get(commandName);
        if (command == null) {
            return UNKNOWN_COMMAND_EXCEPTION + commands.keySet();
        }
        
        return command.execute(args);
    }
}