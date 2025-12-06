package com.cmpe343.project2.model;

/**
 * DTO representing a flexible search query for contacts.
 */
public class SearchCriteria {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Integer birthMonth;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(Integer birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int activeCriteriaCount() {
        int count = 0;
        if (firstName != null && !firstName.isBlank())
            count++;
        if (lastName != null && !lastName.isBlank())
            count++;
        if (phone != null && !phone.isBlank())
            count++;
        if (email != null && !email.isBlank())
            count++;
        if (birthMonth != null)
            count++;
        return count;
    }
}

