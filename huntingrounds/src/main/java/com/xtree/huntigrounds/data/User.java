package com.xtree.huntigrounds.data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "hunter")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "might")
    private int might;

    @Column(name = "area_limit")
    private int limit;

    @OneToMany(mappedBy="owner")
    Set<Spot> spotsOwned;

    @OneToMany(mappedBy="user")
    Set<Pwning> pwnings;

    //    -------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getMight() {
        return might;
    }

    public void setMight(int might) {
        this.might = might;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Set<Spot> getSpotsOwned() {
        return spotsOwned;
    }

    public void setSpotsOwned(Set<Spot> spotsOwned) {
        this.spotsOwned = spotsOwned;
    }

    public Set<Pwning> getPwnings() {
        return pwnings;
    }

    public void setPwnings(Set<Pwning> pwnings) {
        this.pwnings = pwnings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
