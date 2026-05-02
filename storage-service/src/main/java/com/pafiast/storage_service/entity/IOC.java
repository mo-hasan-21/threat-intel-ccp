package com.pafiast.storage_service.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "iocs", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"ioc_value", "ioc_type", "source"}))
public class IOC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ioc_value", nullable = false, length = 255)
    private String iocValue;

    @Column(name = "ioc_type", nullable = false, length = 50)
    private String iocType;

    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @Column(name = "severity_score")
    private Integer severityScore;

    @Column(name = "validated")
    private Boolean validated;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors
    public IOC() {}

    public IOC(String iocValue, String iocType, String source) {
        this.iocValue = iocValue;
        this.iocType = iocType;
        this.source = source;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIocValue() { return iocValue; }
    public void setIocValue(String iocValue) { this.iocValue = iocValue; }

    public String getIocType() { return iocType; }
    public void setIocType(String iocType) { this.iocType = iocType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Integer getSeverityScore() { return severityScore; }
    public void setSeverityScore(Integer severityScore) { this.severityScore = severityScore; }

    public Boolean getValidated() { return validated; }
    public void setValidated(Boolean validated) { this.validated = validated; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "IOC{" +
                "id=" + id +
                ", iocValue='" + iocValue + '\'' +
                ", iocType='" + iocType + '\'' +
                ", severityScore=" + severityScore +
                '}';
    }
}