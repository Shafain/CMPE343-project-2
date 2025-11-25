package com.cmpe343.project2.command;

/**
 * The Command interface for the Command Design Pattern.
 * Enables operations to be executed and undone.
 */
public interface Command {
    /**
     * Executes the operation.
     * 
     * @return true if successful, false otherwise.
     */
    boolean execute();

    /**
     * Reverses the operation carried out by execute().
     */
    void undo();
}