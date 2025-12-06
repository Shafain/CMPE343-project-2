package com.cmpe343.project2.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a Contact record mapped to the {@code contacts} table and
 * encapsulates all personal and professional details required by the console
 * application. The class is intentionally focused on data encapsulation:
 * <ol>
 * <li>Fields mirror the database columns so DAO layers can hydrate instances
 *     without additional mapping logic.</li>
 * <li>Accessor and mutator methods provide controlled read/write access to each
 *     property.</li>
 * <li>Helper methods such as {@link #getFullName()} and {@link #toString()}
 *     expose formatted representations consumed by the UI.</li>
 * </ol>
 *
 * @author Raul Ibrahimov
 * @author Akhmed Nazarov
 * @author Omirbek Ubaidayev
 * @author Kuandyk Kyrykbayev
 */
public class Contact {
    private int contactId;
    private String firstName;
    private String middleName; // Optional
    private String lastName;
    private String nickname;
    private String phonePrimary;
    private String phoneSecondary; // Optional
    private String email;
    private String linkedinUrl; // Optional
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors

    /**
     * Creates an empty contact instance. This constructor is typically used by
     * DAO layers before individual properties are populated via setters.
     */
    public Contact() {
    }

    /**
     * Builds a fully defined contact without timestamp fields. The constructor
     * steps through each required column and assigns the provided values so
     * that DAOs can persist the object without additional mutation.
     *
     * @param contactId      unique identifier from the database primary key
     * @param firstName      legal first name
     * @param middleName     optional middle name or null
     * @param lastName       legal last name
     * @param nickname       informal nickname used by the UI
     * @param phonePrimary   main phone number used for search and display
     * @param phoneSecondary optional backup phone number
     * @param email          contact email address
     * @param linkedinUrl    optional LinkedIn profile URL
     * @param birthDate      date of birth for reporting and filtering
     */
    public Contact(int contactId, String firstName, String middleName, String lastName,
            String nickname, String phonePrimary, String phoneSecondary,
            String email, String linkedinUrl, LocalDate birthDate) {
        this.contactId = contactId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.birthDate = birthDate;
    }

    public Contact(int contactId, String firstName, String middleName, String lastName,
            String nickname, String phonePrimary, String phoneSecondary,
            String email, String linkedinUrl, LocalDate birthDate,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(contactId, firstName, middleName, lastName, nickname, phonePrimary, phoneSecondary, email, linkedinUrl,
                birthDate);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters (Encapsulation)

    /**
     * Retrieves the database identifier for the contact.
     *
     * @return unique contact ID
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Updates the contact identifier, typically after an insert operation
     * assigns a generated key.
     *
     * @param contactId new identifier value
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Provides the stored first name.
     *
     * @return first name string
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Updates the first name value used in full-name displays and searches.
     *
     * @param firstName new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Accesses the optional middle name.
     *
     * @return middle name or {@code null} when not provided
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the optional middle name field.
     *
     * @param middleName middle name value or {@code null}
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Returns the stored last name.
     *
     * @return last name string
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Persists the last name change.
     *
     * @param lastName new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retrieves the nickname used for informal references.
     *
     * @return nickname value
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Updates the nickname field.
     *
     * @param nickname new nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the primary phone number used in searches and list displays.
     *
     * @return main phone number
     */
    public String getPhonePrimary() {
        return phonePrimary;
    }

    /**
     * Stores the primary phone number value.
     *
     * @param phonePrimary updated main phone number
     */
    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    /**
     * Retrieves the optional secondary phone number.
     *
     * @return backup phone number or {@code null}
     */
    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    /**
     * Updates the secondary phone number value.
     *
     * @param phoneSecondary backup phone number
     */
    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    /**
     * Returns the email address associated with the contact.
     *
     * @return email string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the contact's email address.
     *
     * @param email new email value
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Exposes the LinkedIn profile URL when available.
     *
     * @return LinkedIn URL or {@code null}
     */
    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    /**
     * Updates the LinkedIn profile link.
     *
     * @param linkedinUrl new profile URL
     */
    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    /**
     * Returns the stored birth date value.
     *
     * @return birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date for the contact, used in reports and filtering.
     *
     * @param birthDate date of birth
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Reveals the creation timestamp set when the record was inserted.
     *
     * @return creation {@link LocalDateTime}
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Stores the creation timestamp, typically assigned by DAO operations.
     *
     * @param createdAt creation time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the last update timestamp for the record.
     *
     * @return update {@link LocalDateTime}
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the last modified timestamp.
     *
     * @param updatedAt modification time
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Builds the display-ready full name by concatenating first, optional middle
     * (when present) and last names with spaces. The helper ensures the UI never
     * exposes dangling spaces when the middle name is missing.
     *
     * @return concatenated full name
     */
    public String getFullName() {
        String mid = (middleName == null || middleName.isEmpty()) ? "" : middleName + " ";
        return firstName + " " + mid + lastName;
    }

    @Override
    /**
     * Provides a concise debug-friendly string representation that includes the
     * identifier, full name and primary phone number.
     *
     * @return formatted contact summary
     */
    public String toString() {
        return String.format("Contact [ID=%d, Name=%s, Phone=%s]", contactId, getFullName(), phonePrimary);
    }
}