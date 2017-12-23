package com.fourgeeks.test.server.domain.entities;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "note")
@UuidGenerator(name = "UUID")
@Cacheable(false)
public class Note implements CreatedBy {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false, length = 255)
    private String id;
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    @Column(name = "created_at", nullable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category categoryId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Person createdBy;

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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note that = (Note) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Person getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }
}
