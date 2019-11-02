package com.xtree.huntigrounds.data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "spot")
public class Spot {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "might")
    private int might;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @Column(name = "randevouz")
    private Date randevouz;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="owner")
    private User owner;

    @OneToMany(mappedBy="code")
    Set<Pwning> pwnings;

//    -------------------------------------

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMight() {
        return might;
    }

    public void setMight(int might) {
        this.might = might;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRandevouz() {
        return randevouz;
    }

    public void setRandevouz(Date randevouz) {
        this.randevouz = randevouz;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Pwning> getPwnings() {
        return pwnings;
    }

    public void setPwnings(Set<Pwning> pwnings) {
        this.pwnings = pwnings;
    }
}
