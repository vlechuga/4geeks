package com.fourgeeks.test.server.domain.entities;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "person")
@UuidGenerator(name = "UUID")
@Cacheable(false)
@NamedQueries({
        @NamedQuery(name = "Person.findByEmail",
                query = "SELECT p FROM Person p WHERE p.email =:email")
})
public class Person {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false, length = 255)
    private String id;
    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;
    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @Column(name = "role", nullable = true, length = 16)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "status", nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToMany
    @JoinTable(name="PERSON_PERMISSION",
            joinColumns=@JoinColumn(name="PERSON_ID"),
            inverseJoinColumns=@JoinColumn(name="PERMISSION_ID"))
    private Set<Permission> permissions = new HashSet<Permission>();

    @PrePersist
    public void prePersist() {
        this.createdAt = (this.createdAt == null) ? new Date() : this.createdAt;
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotNull
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @NotNull
    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum Role {
        TRAVEL_PLANNER,
        EXPENSE_TRACKER,
        RUNS_TRACKER,
        NOTES_MANAGER,
        ADMIN
    }

    public enum Status {
        ACTIVE,
        SUSPENDED
    }
}
