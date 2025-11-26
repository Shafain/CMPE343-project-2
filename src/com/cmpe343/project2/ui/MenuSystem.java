package com.cmpe343.project2.ui;

import com.cmpe343.project2.command.*;
import com.cmpe343.project2.dao.ContactDAO;
import com.cmpe343.project2.dao.UserDAO;
import com.cmpe343.project2.db.DatabaseConnection;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.model.Role;
import com.cmpe343.project2.model.User;
import com.cmpe343.project2.service.SessionContext;
import com.cmpe343.project2.util.ConsoleColors;
import com.cmpe343.project2.util.InputHelper;

import java.util.List;

/**
 * The primary User Interface controller for the Console Application.
 * Handles:
 * 1. User Authentication.
 * 2. Role-Based Menu Display.
 * 3. Input routing to DAOs and Services.
 * 4. ASCII Animations.
 */
public class MenuSystem {

    private final UserDAO userDAO;
    private final ContactDAO contactDAO;
    private final CommandInvoker commandInvoker;

    public MenuSystem() {
        this.userDAO = new UserDAO();
        this.contactDAO = new ContactDAO();
        this.commandInvoker = new CommandInvoker();
    }

    /**
     * Starts the application flow.
     */
    public void start() {
        printAsciiAnimation("STARTUP");

        boolean running = true;
        while (running) {
            if (!SessionContext.getInstance().isLoggedIn()) {
                running = showLoginMenu();
            } else {
                showMainMenu();
            }
        }

        printAsciiAnimation("SHUTDOWN");
        DatabaseConnection.closeConnection();
        InputHelper.close();
    }

    /**
     * Displays the Login Screen.
     * 
     * @return false if user wants to exit, true otherwise.
     */
    private boolean showLoginMenu() {
        System.out.println("\n" + ConsoleColors.BLUE_BOLD + "=== SYSTEM LOGIN ===" + ConsoleColors.RESET);
        System.out.println("1. Login");
        System.out.println("2. Exit Application");

        int choice = InputHelper.readInt("Choose option");

        if (choice == 2)
            return false;
        if (choice == 1) {
            String username = InputHelper.readString("Username");
            String password = InputHelper.readString("Password");

            User user = userDAO.authenticate(username, password);
            if (user != null) {
                SessionContext.getInstance().login(user);
                ConsoleColors.printSuccess("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
            } else {
                ConsoleColors.printError("Invalid credentials. Please try again.");
            }
        }
        return true;
    }

    /**
     * Displays the main menu based on the User's Role.
     * Implements the Role-Based Access Control logic.
     */
    private void showMainMenu() {
        User currentUser = SessionContext.getInstance().getCurrentUser();
        Role role = currentUser.getRole();

        System.out.println("\n" + ConsoleColors.PURPLE_BOLD + "=== MAIN MENU ===" + ConsoleColors.RESET);
        System.out.println("User: " + currentUser.getFullName());
        System.out.println("Role: " + role);
        System.out.println("-----------------------------");

        // 1. Common Operations (All Roles)
        System.out.println("1. List All Contacts");
        System.out.println("2. Search Contacts (Single Field)");
        System.out.println("3. Advanced Search (Multi-Field)");
        System.out.println("4. Sort Contacts");
        System.out.println("5. Change Password");
        System.out.println("6. Logout");

        // 2. Role Specific Operations
        if (role == Role.JUNIOR || role == Role.SENIOR || role == Role.MANAGER) {
            System.out.println("7. Update Contact");
            if (commandInvoker.canUndo()) {
                System.out.println("99. UNDO Last Action");
            }
        }

        if (role == Role.SENIOR || role == Role.MANAGER) {
            System.out.println("8. Add New Contact");
            System.out.println("9. Delete Contact");
        }

        if (role == Role.MANAGER) {
            System.out.println("10. Manager: View Stats");
            System.out.println("11. Manager: Add New User");
            System.out.println("12. Manager: List All Users");
            System.out.println("13. Manager: Delete User");
            System.out.println("14. Manager: Update User Role/Details");
        }

        int choice = InputHelper.readInt("Select Operation");
        processMenuChoice(choice, role);
    }

    /**
     * Processes the user's selection based on their role permissions.
     */
    private void processMenuChoice(int choice, Role role) {
        switch (choice) {
            case 1:
                printContacts(contactDAO.findAll("last_name", true));
                break;
            case 2:
                handleSingleSearch();
                break;
            case 3:
                handleAdvancedSearch();
                break;
            case 4:
                handleSort();
                break;
            case 5:
                handleChangePassword();
                break;
            case 6:
                SessionContext.getInstance().logout();
                break;

            // Junior+
            case 7:
                if (checkPermission(role, Role.JUNIOR))
                    handleUpdateContact();
                break;

            // Senior+
            case 8:
                if (checkPermission(role, Role.SENIOR))
                    handleAddContact();
                break;
            case 9:
                if (checkPermission(role, Role.SENIOR))
                    handleDeleteContact();
                break;

            // Manager Only
            case 10:
                if (checkPermission(role, Role.MANAGER))
                    System.out.println(contactDAO.getStats());
                break;
            case 11:
                if (checkPermission(role, Role.MANAGER))
                    handleAddUser();
                break;
            case 12:
                if (checkPermission(role, Role.MANAGER))
                    printUsers(userDAO.getAllUsers());
                break;
            case 13:
                if (checkPermission(role, Role.MANAGER))
                    handleDeleteUser();
                break;
            case 14:
                if (checkPermission(role, Role.MANAGER))
                    handleUpdateUser();
                break;

            case 99:
                commandInvoker.undoLastCommand();
                break;

            default:
                ConsoleColors.printError("Invalid Option.");
        }
    }

    // --- OPERATION HANDLERS ---

    private void handleSingleSearch() {
        System.out.println("Fields: first_name, last_name, phone_primary, email");
        String field = InputHelper.readString("Enter field name");
        String value = InputHelper.readString("Enter search value");
        printContacts(contactDAO.searchByField(field, value));
    }

    private void handleAdvancedSearch() {
        ConsoleColors.printInfo("Advanced Search: Leave empty to skip a criteria.");
        String name = InputHelper.readString("Name contains (First or Last)");
        String phone = InputHelper.readString("Phone contains (Primary or Secondary)");

        printContacts(contactDAO.advancedSearch(
                name.isEmpty() ? null : name,
                phone.isEmpty() ? null : phone));
    }

    private void handleSort() {
        System.out.println("Sort by: 1. Last Name, 2. First Name, 3. ID");
        int colChoice = InputHelper.readInt("Choice");
        String col = "last_name";
        if (colChoice == 2)
            col = "first_name";
        if (colChoice == 3)
            col = "contact_id";

        System.out.println("Order: 1. Ascending, 2. Descending");
        boolean asc = InputHelper.readInt("Choice") == 1;

        printContacts(contactDAO.findAll(col, asc));
    }

    private void handleAddContact() {
        Contact c = new Contact();
        System.out.println("--- New Contact Form ---");
        c.setFirstName(InputHelper.readString("First Name"));
        c.setMiddleName(InputHelper.readString("Middle Name (Optional)"));
        c.setLastName(InputHelper.readString("Last Name"));
        c.setNickname(InputHelper.readString("Nickname"));
        c.setPhonePrimary(InputHelper.readPhone("Primary Phone"));
        c.setPhoneSecondary(InputHelper.readString("Secondary Phone (Optional)"));
        // Use the specific Email validator
        c.setEmail(InputHelper.readEmail("Email"));
        c.setLinkedinUrl(InputHelper.readString("LinkedIn URL (Optional)"));
        c.setBirthDate(InputHelper.readDate("Birth Date"));

        // Execute via Command Invoker for Undo capability
        Command cmd = new AddContactCommand(contactDAO, c);
        commandInvoker.executeCommand(cmd);
    }

    private void handleUpdateContact() {
        int id = InputHelper.readInt("Enter Contact ID to Update");
        // Fetch existing
        List<Contact> existing = contactDAO.searchByField("contact_id", String.valueOf(id));
        if (existing.isEmpty()) {
            ConsoleColors.printError("Contact not found.");
            return;
        }
        Contact oldContact = existing.get(0);
        Contact newContact = new Contact(
                oldContact.getContactId(), oldContact.getFirstName(), oldContact.getMiddleName(),
                oldContact.getLastName(), oldContact.getNickname(), oldContact.getPhonePrimary(),
                oldContact.getPhoneSecondary(), oldContact.getEmail(), oldContact.getLinkedinUrl(),
                oldContact.getBirthDate());

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getFirstName() + "]");
        String input = InputHelper.readString("First Name");
        if (!input.isEmpty())
            newContact.setFirstName(input);

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getLastName() + "]");
        input = InputHelper.readString("Last Name");
        if (!input.isEmpty())
            newContact.setLastName(input);

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getPhonePrimary() + "]");
        input = InputHelper.readString("Phone");
        if (!input.isEmpty())
            newContact.setPhonePrimary(input);

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getEmail() + "]");
        while (true) {
            input = InputHelper.readString("Email");
            if (input.isEmpty())
                break; // Keep old email
            // Basic Validation logic duplicated here for the "optional" update flow
            if (input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                newContact.setEmail(input);
                break;
            } else {
                ConsoleColors.printError("Invalid email format.");
            }
        }

        // Execute via Command Invoker
        Command cmd = new UpdateContactCommand(contactDAO, oldContact, newContact);
        commandInvoker.executeCommand(cmd);
    }

    private void handleDeleteContact() {
        int id = InputHelper.readInt("Enter Contact ID to DELETE");
        List<Contact> existing = contactDAO.searchByField("contact_id", String.valueOf(id));
        if (existing.isEmpty()) {
            ConsoleColors.printError("Contact not found.");
            return;
        }
        Command cmd = new DeleteContactCommand(contactDAO, existing.get(0));
        commandInvoker.executeCommand(cmd);
    }

    private void handleAddUser() {
        User u = new User();
        u.setUsername(InputHelper.readString("New Username"));
        String pwd = InputHelper.readString("New Password");
        u.setFirstName(InputHelper.readString("First Name"));
        u.setLastName(InputHelper.readString("Last Name"));

        System.out.println("Roles: TESTER, JUNIOR, SENIOR, MANAGER");
        u.setRole(Role.fromString(InputHelper.readString("Role")));

        if (userDAO.addUser(u, pwd)) {
            ConsoleColors.printSuccess("User created successfully.");
        }
    }

    private void handleDeleteUser() {
        int id = InputHelper.readInt("Enter User ID to DELETE");
        if (userDAO.deleteUser(id)) {
            ConsoleColors.printSuccess("User deleted.");
        } else {
            ConsoleColors.printError("User not found.");
        }
    }

    private void handleUpdateUser() {
        int id = InputHelper.readInt("Enter User ID to Update");
        User existingUser = userDAO.getUserById(id);

        if (existingUser == null) {
            ConsoleColors.printError("User not found.");
            return;
        }

        System.out.println("Updating User: " + existingUser.getUsername());
        ConsoleColors.printInfo("Press Enter to keep current value.");

        String fname = InputHelper.readString("First Name [" + existingUser.getFirstName() + "]");
        if (!fname.isEmpty())
            existingUser.setFirstName(fname);

        String lname = InputHelper.readString("Last Name [" + existingUser.getLastName() + "]");
        if (!lname.isEmpty())
            existingUser.setLastName(lname);

        String roleStr = InputHelper
                .readString("Role [" + existingUser.getRole() + "] (TESTER, JUNIOR, SENIOR, MANAGER)");
        if (!roleStr.isEmpty())
            existingUser.setRole(Role.fromString(roleStr));

        if (userDAO.updateUser(existingUser)) {
            ConsoleColors.printSuccess("User updated successfully.");
        }
    }

    private void handleChangePassword() {
        System.out.println("--- Change Password ---");

        String newPass = InputHelper.readString("Enter new password");
        if (newPass.isEmpty()) {
            ConsoleColors.printError("Password cannot be empty.");
            return;
        }

        String confirmPass = InputHelper.readString("Confirm new password");

        if (!newPass.equals(confirmPass)) {
            ConsoleColors.printError("Passwords do not match! Operation cancelled.");
            return;
        }

        // Get current user ID
        int userId = SessionContext.getInstance().getCurrentUser().getUserId();

        if (userDAO.updatePassword(userId, newPass)) {
            ConsoleColors.printSuccess("Password changed successfully.");
        } else {
            ConsoleColors.printError("Failed to change password.");
        }
    }

    // --- HELPERS ---

    private void printContacts(List<Contact> list) {
        if (list.isEmpty()) {
            ConsoleColors.printWarning("No contacts found.");
            return;
        }
        System.out.printf("%-5s %-20s %-15s %-20s\n", "ID", "Name", "Phone", "Email");
        System.out.println("-------------------------------------------------------------");
        for (Contact c : list) {
            System.out.printf("%-5d %-20s %-15s %-20s\n",
                    c.getContactId(), c.getFullName(), c.getPhonePrimary(), c.getEmail());
        }
        System.out.println("-------------------------------------------------------------");
    }

    private void printUsers(List<User> list) {
        System.out.printf("%-5s %-15s %-15s\n", "ID", "Username", "Role");
        for (User u : list) {
            System.out.printf("%-5d %-15s %-15s\n", u.getUserId(), u.getUsername(), u.getRole());
        }
    }

    private boolean checkPermission(Role currentRole, Role requiredRole) {
        // Simple hierarchy check based on enum ordinal
        // TESTER=0, JUNIOR=1, SENIOR=2, MANAGER=3
        if (currentRole.ordinal() >= requiredRole.ordinal()) {
            return true;
        }
        ConsoleColors.printError("Access Denied. Requires role: " + requiredRole);
        return false;
    }

    private void printAsciiAnimation(String type) {
        if (type.equals("STARTUP")) {
            System.out.println(ConsoleColors.CYAN_BOLD);
            System.out.println("   _____ __  __ _____  ______   ____  __ ___  ");
            System.out.println("  / ____|  \\/  |  __ \\|  ____| |___ \\/ /|__ \\ ");
            System.out.println(" | |    | \\  / | |__) | |__      __) / /_  ) |");
            System.out.println(" | |    | |\\/| |  ___/|  __|    |__ <| '_|/ / ");
            System.out.println(" | |____| |  | | |    | |____   ___) | (_) |_| ");
            System.out.println("  \\_____|_|  |_|_|    |______| |____/ \\___/(_) ");
            System.out.println(ConsoleColors.RESET);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        } else {
            System.out.println(ConsoleColors.YELLOW + "Shutting down... Goodbye!" + ConsoleColors.RESET);
        }
    }
}