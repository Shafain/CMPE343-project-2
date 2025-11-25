package com.cmpe343.project2.command;

import com.cmpe343.project2.dao.ContactDAO;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.util.ConsoleColors;

/**
 * Concrete Command to delete a contact.
 * Undo: Re-creates the deleted contact.
 */
public class DeleteContactCommand implements Command {
    private final ContactDAO contactDAO;
    private final Contact contactToDelete;

    public DeleteContactCommand(ContactDAO contactDAO, Contact contactToDelete) {
        this.contactDAO = contactDAO;
        this.contactToDelete = contactToDelete;
    }

    @Override
    public boolean execute() {
        boolean success = contactDAO.deleteContact(contactToDelete.getContactId());
        if (success) {
            ConsoleColors.printSuccess("Contact deleted.");
        }
        return success;
    }

    @Override
    public void undo() {
        // We insert it back. Note: ID might change because of Auto-Increment,
        // but the data is restored.
        contactDAO.addContact(contactToDelete);
        ConsoleColors.printWarning("Undo: Deleted contact has been restored.");
    }
}