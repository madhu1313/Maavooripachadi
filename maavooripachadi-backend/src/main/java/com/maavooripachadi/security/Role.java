package com.maavooripachadi.security;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sec_role", indexes = @Index(name = "ix_role_name", columnList = "name", unique = true))
public class Role extends BaseEntity {
    @Column(nullable = false, unique = true, length = 64)
    private String name; // e.g., ADMIN
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sec_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
}
