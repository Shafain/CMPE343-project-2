package com.cmpe343.project2.command;

import com.cmpe343.project2.dao.UserDAO;
import com.cmpe343.project2.model.User;
import com.cmpe343.project2.util.ConsoleColors;

/**
 * Command for deleting a user with undo support.
 * Undo re-creates the deleted user with the same id and credentials.
 */
public class DeleteUserCommand implements Command {
    private final UserDAO userDAO;
    private final User userToDelete;

    public DeleteUserCommand(UserDAO userDAO, User userToDelete) {
        this.userDAO = userDAO;
        this.userToDelete = userToDelete;
    }

    @Override
    public boolean execute() {
        boolean success = userDAO.deleteUser(userToDelete.getUserId());
        if (success) {
            ConsoleColors.printSuccess("User deleted.");
        }
        return success;
    }

    @Override
    public void undo() {
        boolean restored = userDAO.restoreUser(userToDelete);
        if (restored) {
            ConsoleColors.printWarning("Undo: Deleted user has been restored.");
        } else {
            ConsoleColors.printError("Undo Failed: Could not restore the deleted user.");
        }
    }
}
