package com.cmpe343.project2.command;

import com.cmpe343.project2.dao.ContactDAO;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.util.ConsoleColors;

/**
 * Concrete Command to update a contact.
 * Undo: Reverts the contact to its previous state.
 */
public class UpdateContactCommand implements Command {
    private final ContactDAO contactDAO;
    private final Contact oldContactState;
    private final Contact newContactState;

    public UpdateContactCommand(ContactDAO contactDAO, Contact oldContactState, Contact newContactState) {
        this.contactDAO = contactDAO;
        this.oldContactState = oldContactState;
        this.newContactState = newContactState;
    }

    @Override
    public boolean execute() {
        boolean success = contactDAO.updateContact(newContactState);
        if (success) {
            ConsoleColors.printSuccess("Contact updated.");
        }
        return success;
    }

    @Override
    public void undo() {
        contactDAO.updateContact(oldContactState);
        ConsoleColors.printWarning("Undo: Contact update reverted to previous state.");
    }
}