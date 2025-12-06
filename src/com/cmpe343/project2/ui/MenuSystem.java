package com.cmpe343.project2.ui;

import com.cmpe343.project2.command.*;
import com.cmpe343.project2.dao.ContactDAO;
import com.cmpe343.project2.dao.UserDAO;
import com.cmpe343.project2.db.DatabaseConnection;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.model.Role;
import com.cmpe343.project2.model.SearchCriteria;
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
    private String lastSortColumn = "last_name";
    private boolean lastSortAsc = true;

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
        while (true) {
            System.out.println("\n" + ConsoleColors.BLUE_BOLD + "=== SYSTEM LOGIN ===" + ConsoleColors.RESET);
            System.out.println("1. Login");
            System.out.println("2. Exit Application");

            int choice = InputHelper.readChoice("Choose option", 1, 2);

            if (choice == 2)
                return false;

            if (choice == 1) {
                attemptLogin();
                if (SessionContext.getInstance().isLoggedIn()) {
                    return true;
                }
            }
        }
    }

    /**
     * Displays the main menu based on the User's Role.
     * Implements the Role-Based Access Control logic.
     */
    private void showMainMenu() {
        User currentUser = SessionContext.getInstance().getCurrentUser();
        Role role = currentUser.getRole();
        boolean stay = true;
        while (stay && SessionContext.getInstance().isLoggedIn()) {
            InputHelper.clearScreen();
            System.out.println("\n" + ConsoleColors.PURPLE_BOLD + "=== MAIN MENU ===" + ConsoleColors.RESET);
            System.out.println("User: " + currentUser.getFullName());
            System.out.println("Role: " + role);
            System.out.println("-----------------------------");

            switch (role) {
                case TESTER:
                    System.out.println("1. List All Contacts");
                    System.out.println("2. Search by selected field or fields");
                    System.out.println("3. Sort Contacts");
                    System.out.println("4. Change Password");
                    System.out.println("5. Logout");
                    stay = processMenuChoice(InputHelper.readChoice("Select Operation", 1, 2, 3, 4, 5), role);
                    break;
                case JUNIOR:
                    System.out.println("1. List All Contacts");
                    System.out.println("2. Search by selected field or fields");
                    System.out.println("3. Sort Contacts");
                    System.out.println("4. Update Contact");
                    System.out.println("5. Undo Last Action");
                    System.out.println("6. Change Password");
                    System.out.println("7. Logout");
                    stay = processMenuChoice(InputHelper.readChoice("Select Operation", 1, 2, 3, 4, 5, 6, 7), role);
                    break;
                case SENIOR:
                    System.out.println("1. List All Contacts");
                    System.out.println("2. Search by selected field or fields");
                    System.out.println("3. Sort Contacts");
                    System.out.println("4. Update Contact");
                    System.out.println("5. Add New Contact");
                    System.out.println("6. Delete Contact");
                    System.out.println("7. Undo Last Action");
                    System.out.println("8. Change Password");
                    System.out.println("9. Logout");
                    stay = processMenuChoice(InputHelper.readChoice("Select Operation", 1, 2, 3, 4, 5, 6, 7, 8, 9),
                            role);
                    break;
                case MANAGER:
                    System.out.println("1. Contacts Statistical Info");
                    System.out.println("2. List All Users");
                    System.out.println("3. Update Existing User");
                    System.out.println("4. Add/Employ New User");
                    System.out.println("5. Delete/Fire Existing User");
                    System.out.println("6. Change Password");
                    System.out.println("7. Logout");
                    stay = processMenuChoice(InputHelper.readChoice("Select Operation", 1, 2, 3, 4, 5, 6, 7),
                            role);
                    break;
            }
        }
    }

    /**
     * Processes the user's selection based on their role permissions.
     */
    private boolean processMenuChoice(int choice, Role role) {
        switch (role) {
            case TESTER:
                return handleTesterChoice(choice);
            case JUNIOR:
                return handleJuniorChoice(choice);
            case SENIOR:
                return handleSeniorChoice(choice);
            case MANAGER:
                return handleManagerChoice(choice);
            default:
                return true;
        }
    }

    // --- OPERATION HANDLERS ---

    private void attemptLogin() {
        while (true) {
            String username = InputHelper.readValidatedString(
                    "Username (letters/digits, start with a letter or type 'back' to return)",
                    false, "[A-Za-z][A-Za-z0-9_]*",
                    "Username must start with a letter and contain only letters, digits, or underscores.", true);
            if (username == null)
                return;

            String password = InputHelper.readString("Password");

            User user = userDAO.authenticate(username, password);
            if (user != null) {
                SessionContext.getInstance().login(user);
                ConsoleColors.printSuccess("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
                return;
            }

            ConsoleColors.printError("Invalid credentials. Please try again.");
        }
    }

    private boolean handleTesterChoice(int choice) {
        switch (choice) {
            case 1:
                printContacts(getSortedContacts());
                return true;
            case 2:
                handleSearchMenu();
                return true;
            case 3:
                handleSort();
                return true;
            case 4:
                handleChangePassword();
                return true;
            case 5:
                SessionContext.getInstance().logout();
                return false;
            default:
                ConsoleColors.printError("Invalid Option.");
                return true;
        }
    }

    private boolean handleJuniorChoice(int choice) {
        switch (choice) {
            case 1:
                printContacts(getSortedContacts());
                return true;
            case 2:
                handleSearchMenu();
                return true;
            case 3:
                handleSort();
                return true;
            case 4:
                handleUpdateContact();
                return true;
            case 5:
                commandInvoker.undoLastCommand();
                return true;
            case 6:
                handleChangePassword();
                return true;
            case 7:
                SessionContext.getInstance().logout();
                return false;
            default:
                ConsoleColors.printError("Invalid Option.");
                return true;
        }
    }

    private boolean handleSeniorChoice(int choice) {
        switch (choice) {
            case 1:
                printContacts(getSortedContacts());
                return true;
            case 2:
                handleSearchMenu();
                return true;
            case 3:
                handleSort();
                return true;
            case 4:
                handleUpdateContact();
                return true;
            case 5:
                handleAddContact();
                return true;
            case 6:
                handleDeleteContact();
                return true;
            case 7:
                commandInvoker.undoLastCommand();
                return true;
            case 8:
                handleChangePassword();
                return true;
            case 9:
                SessionContext.getInstance().logout();
                return false;
            default:
                ConsoleColors.printError("Invalid Option.");
                return true;
        }
    }

    private boolean handleManagerChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println(contactDAO.getStats());
                return true;
            case 2:
                printUsers(userDAO.getAllUsers());
                return true;
            case 3:
                handleUpdateUser();
                return true;
            case 4:
                handleAddUser();
                return true;
            case 5:
                handleDeleteUser();
                return true;
            case 6:
                handleChangePassword();
                return true;
            case 7:
                SessionContext.getInstance().logout();
                return false;
            default:
                ConsoleColors.printError("Invalid Option.");
                return true;
        }
    }

    private void handleSearchMenu() {
        ConsoleColors.printInfo("Type 'back' at any prompt to return without losing your place.");
        while (true) {
            System.out.println("1. Search Contacts (Single Field)");
            System.out.println("2. Advanced Search (Multi-Field)");
            System.out.println("3. Back to Main Menu");
            int choice = InputHelper.readChoice("Choose option", 1, 2, 3);

            if (choice == 3)
                return;
            if (choice == 1) {
                handleSingleFieldSearch();
            }
            if (choice == 2) {
                handleMultiFieldSearch();
            }
        }
    }

    private void handleSingleFieldSearch() {
        while (true) {
            ConsoleColors.printInfo("Single-field search supports partial matches. Choose a field below.");
            System.out.println("1. Search by First Name");
            System.out.println("2. Search by Last Name");
            System.out.println("3. Search by Phone Number (primary or secondary)");
            System.out.println("4. Back");
            int fieldChoice = InputHelper.readChoice("Field", 1, 2, 3, 4);

            if (fieldChoice == 4)
                return;

            String value;
            String column;
            if (fieldChoice == 1) {
                column = "first_name";
                value = InputHelper.readValidatedString("Enter first name fragment", false, "[A-Za-z\\-\\s]+",
                        "Use letters, spaces or hyphens only.", true);
            } else if (fieldChoice == 2) {
                column = "last_name";
                value = InputHelper.readValidatedString("Enter last name fragment", false, "[A-Za-z\\-\\s]+",
                        "Use letters, spaces or hyphens only.", true);
            } else {
                column = "phone_primary";
                value = InputHelper.readValidatedString("Enter phone digits fragment", false, "[0-9+\\-\\s]+",
                        "Use digits only for phone search.", true);
            }

            if (value == null)
                return;

            List<Contact> results = ("phone_primary".equals(column))
                    ? contactDAO.searchByCriteria(buildPhoneOnlyCriteria(value))
                    : contactDAO.searchByField(column, value);

            if (results.isEmpty()) {
                ConsoleColors.printWarning("No contacts found.");
                int retry = InputHelper.readChoice("Try again? 1=Yes, 2=Back", 1, 2);
                if (retry == 2)
                    return;
            } else {
                printContacts(results);
                return;
            }
        }
    }

    private void handleMultiFieldSearch() {
        while (true) {
            ConsoleColors.printInfo(
                    "Provide at least two fields. Partial text is accepted. Type 'back' to cancel any step.");
            String firstName = InputHelper.readValidatedString("First Name contains", true, "[A-Za-z\\-\\s]*",
                    "Use letters, spaces or hyphens only.", true);
            if (firstName == null)
                return;
            String lastName = InputHelper.readValidatedString("Last Name contains", true, "[A-Za-z\\-\\s]*",
                    "Use letters, spaces or hyphens only.", true);
            if (lastName == null)
                return;
            String phone = InputHelper.readValidatedString("Phone contains", true, "[0-9+\\-\\s]*",
                    "Use digits only for phone search.", true);
            if (phone == null)
                return;
            String email = InputHelper.readValidatedString("Email contains", true, "[A-Za-z0-9@._+-]*",
                    "Use typical email characters only.", true);
            if (email == null)
                return;

            Integer month = null;
            while (true) {
                String monthInput = InputHelper.readString("Birth month (1-12, optional, 'back' to cancel)");
                if ("back".equalsIgnoreCase(monthInput))
                    return;
                if (monthInput.isEmpty())
                    break;
                try {
                    int m = Integer.parseInt(monthInput);
                    if (m < 1 || m > 12) {
                        ConsoleColors.printError("Month must be between 1 and 12.");
                        continue;
                    }
                    month = m;
                    break;
                } catch (NumberFormatException e) {
                    ConsoleColors.printError("Month must be numeric.");
                }
            }

            SearchCriteria criteria = new SearchCriteria();
            criteria.setFirstName(firstName);
            criteria.setLastName(lastName);
            criteria.setPhone(phone);
            criteria.setEmail(email);
            criteria.setBirthMonth(month);

            if (criteria.activeCriteriaCount() < 2) {
                ConsoleColors.printWarning("Please provide at least two filters for advanced search.");
                continue;
            }

            List<Contact> results = contactDAO.searchByCriteria(criteria);
            if (results.isEmpty()) {
                ConsoleColors.printWarning("No contacts found.");
                int retry = InputHelper.readChoice("Try again? 1=Yes, 2=Back", 1, 2);
                if (retry == 2)
                    return;
            } else {
                printContacts(results);
                return;
            }
        }
    }

    private SearchCriteria buildPhoneOnlyCriteria(String value) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setPhone(value);
        return criteria;
    }

    private void handleSort() {
        ConsoleColors.printInfo("Select a field to sort by. Sorting persists until changed.");
        System.out.println("1. ID");
        System.out.println("2. First Name");
        System.out.println("3. Last Name");
        System.out.println("4. Email");
        System.out.println("5. Primary Phone");
        int colChoice = InputHelper.readChoice("Field", 1, 2, 3, 4, 5);
        String col = switch (colChoice) {
            case 1 -> "contact_id";
            case 2 -> "first_name";
            case 3 -> "last_name";
            case 4 -> "email";
            case 5 -> "phone_primary";
            default -> "last_name";
        };

        System.out.println("Order: 1. Ascending, 2. Descending");
        boolean asc = InputHelper.readChoice("Order", 1, 2) == 1;

        lastSortColumn = col;
        lastSortAsc = asc;
        printContacts(getSortedContacts());
    }

    private void handleAddContact() {
        Contact c = new Contact();
        System.out.println("--- New Contact Form ---");
        ConsoleColors.printInfo("Type 'back' to cancel and return.");
        c.setFirstName(InputHelper.readName("First Name", true));
        if (c.getFirstName() == null)
            return;
        c.setMiddleName(InputHelper.readOptionalName("Middle Name", true));
        if (c.getMiddleName() == null)
            return;
        c.setLastName(InputHelper.readName("Last Name", true));
        if (c.getLastName() == null)
            return;
        String nick = InputHelper.readOptionalName("Nickname", true);
        if (nick == null)
            return;
        c.setNickname(nick);
        c.setPhonePrimary(InputHelper.readPhone("Primary Phone"));
        String secondary = InputHelper.readOptionalPhone("Secondary Phone", true);
        if (secondary == null)
            return;
        c.setPhoneSecondary(secondary);
        String email = InputHelper.readOptionalEmail("Email", true);
        if (email == null)
            return;
        if (email.isEmpty()) {
            ConsoleColors.printError("Email is required.");
            return;
        }
        c.setEmail(email);
        c.setLinkedinUrl(InputHelper.readValidatedString("LinkedIn URL", true, "[A-Za-z0-9:/._-]*",
                "Only URL-safe characters are allowed.", true));
        if (c.getLinkedinUrl() == null)
            return;
        c.setBirthDate(InputHelper.readDate("Birth Date"));

        Command cmd = new AddContactCommand(contactDAO, c);
        commandInvoker.executeCommand(cmd);
    }

    private void handleUpdateContact() {
        printContacts(getSortedContacts());
        String idInput = InputHelper.readString("Enter Contact ID to Update (or type 'back')");
        if ("back".equalsIgnoreCase(idInput))
            return;
        int id;
        try {
            id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            ConsoleColors.printError("Contact ID must be numeric.");
            return;
        }

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
                oldContact.getBirthDate(), oldContact.getCreatedAt(), oldContact.getUpdatedAt());

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getFirstName() + "]");
        String input = InputHelper.readValidatedString("First Name", true, "[A-Za-z\\-\\s]*",
                "Use letters, spaces or hyphens only.", true);
        if (input == null)
            return;
        if (!input.isEmpty())
            newContact.setFirstName(input);

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getLastName() + "]");
        input = InputHelper.readValidatedString("Last Name", true, "[A-Za-z\\-\\s]*",
                "Use letters, spaces or hyphens only.", true);
        if (input == null)
            return;
        if (!input.isEmpty())
            newContact.setLastName(input);

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getPhonePrimary() + "]");
        input = InputHelper.readValidatedString("Phone", true, "[0-9+\\-\\s]*",
                "Use digits only for phone numbers.", true);
        if (input == null)
            return;
        if (!input.isEmpty())
            newContact.setPhonePrimary(input);

        ConsoleColors.printInfo("Enter new value or press enter to keep [" + oldContact.getEmail() + "]");
        input = InputHelper.readOptionalEmail("Email", true);
        if (input == null)
            return;
        if (!input.isEmpty())
            newContact.setEmail(input);

        Command cmd = new UpdateContactCommand(contactDAO, oldContact, newContact);
        commandInvoker.executeCommand(cmd);
    }

    private void handleDeleteContact() {
        printContacts(getSortedContacts());
        String idInput = InputHelper.readString("Enter Contact ID to DELETE (or type 'back')");
        if ("back".equalsIgnoreCase(idInput))
            return;
        int id;
        try {
            id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            ConsoleColors.printError("Contact ID must be numeric.");
            return;
        }

        List<Contact> existing = contactDAO.searchByField("contact_id", String.valueOf(id));
        if (existing.isEmpty()) {
            ConsoleColors.printError("Contact not found.");
            return;
        }

        int confirm = InputHelper.readChoice("Are you sure you want to delete this contact? 1=Yes 2=No", 1, 2);
        if (confirm == 2)
            return;

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
        System.out.printf("%-4s %-12s %-12s %-12s %-12s %-14s %-14s %-25s %-18s %-12s %-20s %-20s%n",
                "ID", "First", "Middle", "Last", "Nickname", "Phone (P)", "Phone (S)", "Email", "LinkedIn",
                "Birth", "Created", "Updated");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------");
        for (Contact c : list) {
            System.out.printf("%-4d %-12s %-12s %-12s %-12s %-14s %-14s %-25s %-18s %-12s %-20s %-20s%n",
                    c.getContactId(),
                    safe(c.getFirstName()),
                    safe(c.getMiddleName()),
                    safe(c.getLastName()),
                    safe(c.getNickname()),
                    safe(c.getPhonePrimary()),
                    safe(c.getPhoneSecondary()),
                    safe(c.getEmail()),
                    safe(c.getLinkedinUrl()),
                    c.getBirthDate() == null ? "" : c.getBirthDate().toString(),
                    c.getCreatedAt() == null ? "" : c.getCreatedAt().toString(),
                    c.getUpdatedAt() == null ? "" : c.getUpdatedAt().toString());
        }
        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------");
    }

    private List<Contact> getSortedContacts() {
        return contactDAO.findAll(lastSortColumn, lastSortAsc);
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
            System.out.println(ConsoleColors.PURPLE_BOLD);
            System.out.println("  ______ _           _   _                _           ");
            System.out.println(" |  ____| |         | | (_)              | |          ");
            System.out.println(" | |__  | | ___  ___| |_ _  ___  _ __  __| | ___ _ __ ");
            System.out.println(" |  __| | |/ _ \\/ __| __| |/ _ \\| '_ \\/ _` |/ _ \\ '__|");
            System.out.println(" | |____| |  __/ (__| |_| | (_) | | | | (_| |  __/ |   ");
            System.out.println(" |______|_|\\___|\\___|\\__|_|\\___/|_| |_|\\__,_|\\___|_|   ");
            System.out.println(ConsoleColors.RESET);
        }
    }
}