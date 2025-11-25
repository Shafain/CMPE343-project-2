package com.cmpe343.project2.command;

import com.cmpe343.project2.util.ConsoleColors;
import java.util.Stack;

/**
 * Invoker class in the Command Pattern.
 * Manages the execution history and performs the actual Undo logic.
 */
public class CommandInvoker {
    private final Stack<Command> commandHistory = new Stack<>();

    /**
     * Executes a command and pushes it to the history stack if successful.
     */
    public void executeCommand(Command cmd) {
        if (cmd.execute()) {
            commandHistory.push(cmd);
        }
    }

    /**
     * Pops the last command from the stack and calls its undo method.
     */
    public void undoLastCommand() {
        if (commandHistory.isEmpty()) {
            ConsoleColors.printInfo("Nothing to undo.");
            return;
        }

        Command lastCmd = commandHistory.pop();
        lastCmd.undo();
    }

    /**
     * Checks if there are actions to undo.
     */
    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }
}