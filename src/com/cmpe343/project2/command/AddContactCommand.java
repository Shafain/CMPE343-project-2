package com.cmpe343.project2.command;

import com.cmpe343.project2.dao.ContactDAO;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.util.ConsoleColors;

import java.util.List;

/**
 * Concrete Command to add a contact.
 * Undo: Deletes the added contact.
 */
public class AddContactCommand implements Command {
    private final ContactDAO contactDAO;
    private final Contact contactToAdd;
    // We need to track the ID assigned by the DB to delete it later
    private int assignedId = -1;

    public AddContactCommand(ContactDAO contactDAO, Contact contactToAdd) {
        this.contactDAO = contactDAO;
        this.contactToAdd = contactToAdd;
    }

    @Override
    public boolean execute() {
        boolean success = contactDAO.addContact(contactToAdd);
        if (success) {
            // In a real scenario, we would fetch the last ID.
            // For this scope, we search for the contact we just added to get its ID for
            // undo purposes.
            // This assumes unique phone numbers or exact matches.
            List<Contact> search = contactDAO.searchByField("phone_primary", contactToAdd.getPhonePrimary());
            if (!search.isEmpty()) {
                this.assignedId = search.get(0).getContactId();
            }
            ConsoleColors.printSuccess("Contact added successfully.");
        }
        return success;
    }

    @Override
    public void undo() {
        if (assignedId != -1) {
            contactDAO.deleteContact(assignedId);
            ConsoleColors.printWarning("Undo: Added contact has been removed.");
        } else {
            ConsoleColors.printError("Undo Failed: Could not identify the added record.");
        }
    }
}