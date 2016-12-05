package ru.spbau;

import org.mongodb.morphia.annotations.*;

@Entity
public class Telephone implements Comparable<Telephone> {
    @Id
    private String id;
    private String name;
    private String telephoneNumber;

    public Telephone() {}

    public Telephone(String name, String telephoneNumber) {
        this.id = name + telephoneNumber;
        this.name = name;
        this.telephoneNumber = telephoneNumber;
    }

    public void print() {
        System.out.println(name + ": " + telephoneNumber);
    }

    public String getName() {
        return name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    @Override
    public int compareTo(Telephone telephone) {
        return id.compareTo(telephone.id);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Telephone && compareTo((Telephone)o) == 0);
    }
}
