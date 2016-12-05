package ru.spbau;

import org.mongodb.morphia.annotations.*;

/** Запись в базе данных, хранящая имя и номер телефона.*/
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

    /** Выводит запись на экран.*/
    public void print() {
        System.out.println(name + ": " + telephoneNumber);
    }

    /** Возвращает имя.*/
    public String getName() {
        return name;
    }

    /** Возвращает номер телефона.*/
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
