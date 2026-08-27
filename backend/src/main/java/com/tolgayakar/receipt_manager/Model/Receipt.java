package com.tolgayakar.receipt_manager.Model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String file_path;
    private String name;
    private String description;
    private Instant create_at;
    private Boolean is_deleted;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private RmUser user_id;

    public Long getId() {
        return id;
    }

    public void setFilePath(String filePath) {
        this.file_path = filePath;
    }

    public String getFilePath() {
        return file_path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String desc){
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

    public void setCreatedAt(Instant createAt) {
        this.create_at = createAt;
    }

    public Instant getCreateAt() {
        return create_at;
    }

    public void isDeleted(Boolean isDeleted) {
        this.is_deleted = isDeleted;
    }

    public Boolean getDeleted() {
        return is_deleted;
    }

    public RmUser getUserId() {
        return user_id;
    }
}
