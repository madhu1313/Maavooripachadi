package com.maavooripachadi.security;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sec_user", indexes = {
        @Index(name = "ix_user_email", columnList = "email", unique = true),
        @Index(name = "ix_user_phone", columnList = "phone", unique = true)
})
public class AppUser extends BaseEntity {
    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true, unique = true, length = 32)
    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    private String fullName;
    private Boolean enabled = Boolean.TRUE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sec_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public String getLoginIdentifier() {
        return email != null ? email : phone;
    }
}
