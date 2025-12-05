package com.cmpe343.project2.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a Contact in the system.
 * Maps to the 'contacts' table in the database.
 * Stores personal and professional details of a contact.
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
    public Contact() {
    }

    /**
     * Full constructor for initializing a contact object.
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

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhonePrimary() {
        return phonePrimary;
    }

    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return Full name concatenation logic.
     */
    public String getFullName() {
        String mid = (middleName == null || middleName.isEmpty()) ? "" : middleName + " ";
        return firstName + " " + mid + lastName;
    }

    @Override
    public String toString() {
        return String.format("Contact [ID=%d, Name=%s, Phone=%s]", contactId, getFullName(), phonePrimary);
    }
}