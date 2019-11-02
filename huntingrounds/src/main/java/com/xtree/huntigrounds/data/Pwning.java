package com.xtree.huntigrounds.data;

import javax.persistence.*;

@Entity
@Table(name = "pwning")
public class Pwning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="code")
    private Spot code;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Spot getCode() {
        return code;
    }

    public void setCode(Spot code) {
        this.code = code;
    }
}
