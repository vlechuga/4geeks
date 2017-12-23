package com.fourgeeks.test.server.domain.entities;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "transaction")
@UuidGenerator(name = "UUID")
@Cacheable(false)
public class Transaction implements CreatedBy{

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false, length = 255)
    private String id;
    @Column(name = "subject", nullable = false, length = 255)
    private String subject;
    @Column(name = "amount", nullable = false, precision = 20, scale = 4)
    private BigDecimal amount;
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(name = "type", nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at", nullable = false)
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
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @NotNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    @NotNull
    public void setDate(Date date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    @NotNull
    public void setType(TransactionType type) {
        this.type = type;
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
        return this.categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public Person getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum TransactionType {
        WITHDRAWAL,
        DEPOSIT
    }

}
