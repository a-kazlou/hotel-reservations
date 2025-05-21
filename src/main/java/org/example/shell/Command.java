package org.example.shell;

public interface Command {
    String execute(String[] args);
    String getName();
    String getDescription();
}