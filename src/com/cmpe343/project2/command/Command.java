package com.cmpe343.project2.command;

/**
 * Defines the contract for executable actions within the Command design
 * pattern used throughout the application. Each implementation captures all
 * data needed to perform a business operation and to revert it later. Typical
 * execution flow:
 * <ol>
 * <li>{@link #execute()} applies the change (insert/update/delete).</li>
 * <li>{@link #undo()} restores the previous state when the user triggers an
 *     undo action.</li>
 * </ol>
 *
 * @author Raul Ibrahimov
 * @author Akhmed Nazarov
 * @author Omirbek Ubaidayev
 * @author Kuandyk Kyrykbayev
 */
public interface Command {
    /**
     * Executes the underlying operation. Implementations usually validate
     * prerequisites, perform the data change, and return whether the action
     * succeeded so the invoker can decide whether to record it for undo
     * tracking.
     *
     * @return true if successful, false otherwise.
     */
    boolean execute();

    /**
     * Reverses the state mutated by {@link #execute()} using any information
     * captured during execution. Implementations must be idempotent so multiple
     * undo attempts do not corrupt data.
     */
    void undo();
}