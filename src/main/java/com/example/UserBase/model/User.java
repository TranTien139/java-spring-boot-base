package com.example.UserBase.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="full_name", nullable = false)
    private String fullName;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="password", nullable =  false)
    private String password;

    public long getId() {return id;}
    public void setId(long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
