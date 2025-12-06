package com.cmpe343.project2.db;

import com.cmpe343.project2.dao.ContactDAO;
import com.cmpe343.project2.dao.UserDAO;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.model.Role;
import com.cmpe343.project2.model.User;
import com.cmpe343.project2.util.ConsoleColors;

import java.time.LocalDate;
import java.util.Random;

/**
 * Utility class to seed the database with initial data.
 * Ensures the project requirements (4 users, 50 contacts) are met instantly.
 */
public class DataSeeder {

    public static void seedData() {
        UserDAO userDAO = new UserDAO();
        ContactDAO contactDAO = new ContactDAO();

        // 1. Seed Users if empty
        if (userDAO.getAllUsers().isEmpty()) {
            ConsoleColors.printInfo("Seeding Users...");
            // Using passwords 'tt', 'jd', 'sd', 'man' as per project brief?
            // Brief says: un: tt PSW: tt
            userDAO.addUser(new User("tt", "", "Test", "User", Role.TESTER), "tt");
            userDAO.addUser(new User("jd", "", "Junior", "Dev", Role.JUNIOR), "jd");
            userDAO.addUser(new User("sd", "", "Senior", "Dev", Role.SENIOR), "sd");
            userDAO.addUser(new User("man", "", "General", "Manager", Role.MANAGER), "man");
            ConsoleColors.printSuccess("Users seeded!");
        }

        // 2. Seed 50 Contacts if empty
        if (contactDAO.findAll("contact_id", true).isEmpty()) {
            ConsoleColors.printInfo("Seeding 50 Dummy Contacts...");
            String[] firstNames = { "Ahmet", "Mehmet", "Ayse", "Fatma", "John", "Jane", "Ali", "Veli", "Zeynep",
                    "Elif", "Sofia", "Liam", "Noah", "Olivia", "Mia", "Lucas", "Emir", "Aisha", "Diego", "Natalia" };
            String[] lastNames = { "Yilmaz", "Demir", "Kaya", "Celik", "Smith", "Doe", "Ozturk", "Arslan", "Koc",
                    "Sahin", "Garcia", "Khan", "Brown", "Ivanov", "Suzuki", "Nguyen", "Lopez", "Patel", "Hansen",
                    "Petrova" };

            Random rand = new Random();

            for (int i = 0; i < 50; i++) {
                String fn = firstNames[rand.nextInt(firstNames.length)];
                String ln = lastNames[rand.nextInt(lastNames.length)];

                Contact c = new Contact();
                c.setFirstName(fn);
                c.setLastName(ln);
                c.setNickname(fn.substring(0, 2));
                c.setPhonePrimary("555" + String.format("%07d", rand.nextInt(10000000)));
                c.setEmail(fn.toLowerCase() + "." + ln.toLowerCase() + "@example.com");
                c.setBirthDate(LocalDate.of(1970 + rand.nextInt(30), 1 + rand.nextInt(12), 1 + rand.nextInt(28)));

                // Optional fields random fill
                if (rand.nextBoolean())
                    c.setLinkedinUrl("linkedin.com/in/" + fn + ln);

                contactDAO.addContact(c);
            }
            ConsoleColors.printSuccess("50 Contacts seeded!");
        }
    }
}