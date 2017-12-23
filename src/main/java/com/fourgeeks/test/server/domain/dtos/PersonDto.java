package com.fourgeeks.test.server.domain.dtos;

import com.fourgeeks.test.server.domain.entities.Person;

import java.util.Date;

public class PersonDto {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private Person.Role role;
    private Person.Status status;
    private Date createdAt;
    private Date updatedAt;

    public PersonDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Person.Role getRole() {
        return role;
    }

    public void setRole(Person.Role role) {
        this.role = role;
    }

    public Person.Status getStatus() {
        return status;
    }

    public void setStatus(Person.Status status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
