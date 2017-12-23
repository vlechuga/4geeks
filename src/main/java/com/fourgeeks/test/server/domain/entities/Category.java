package com.fourgeeks.test.server.domain.entities;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "category")
@UuidGenerator(name = "UUID")
@Cacheable(false)
@NamedQueries({
        @NamedQuery(name = "Category.findByName",
                query = "SELECT c FROM Category c WHERE c.name =:name")
})
public class Category implements CreatedBy {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false, length = 255)
    private String id;
    @Column(name = "name", unique=true, nullable = false, length = 255)
    private String name;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Person createdBy;
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Person getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category that = (Category) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
