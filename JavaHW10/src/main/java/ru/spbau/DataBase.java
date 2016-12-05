package ru.spbau;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;
import java.util.List;

/** Отвечает за работу с базой данных для телефонного справочника.*/
public class DataBase {

    private Morphia morphia;
    private Datastore datastore;

    /** Создает базу данных с переданным названием.*/
    public DataBase(String dataBaseName) {
        morphia = new Morphia();
        morphia.mapPackage("ru.spbau");
        datastore = morphia.createDatastore(new MongoClient(), dataBaseName);
    }

    /** Добавляет в базу данных запись (пару имя-телефон).*/
    public void add(String name, String telephoneNumber) {
        Telephone telephone = new Telephone(name, telephoneNumber);
        datastore.save(telephone);
    }

    /** Удаляет из базы данных запись (пару имя-телефон).*/
    public void delete(String name, String telephoneNumber) {
        datastore.delete(Telephone.class, name + telephoneNumber);
    }

    /** Находит все телефоны по имени.*/
    public List<String> findTelephonesByName(String name) {
        List<String> telephoneNumbers = new ArrayList<>();
        List<Telephone> telephones =  datastore.createQuery(Telephone.class)
                .field("name").equal(name).asList();
        for (Telephone telephone: telephones) {
            telephoneNumbers.add(telephone.getTelephoneNumber());
        }
        return telephoneNumbers;
    }

    /** Находит все имена по телефону.*/
    public List<String> findNamesByTelephone(String telephoneNumber) {
        List<String> names = new ArrayList<>();
        List<Telephone> telephones =  datastore.createQuery(Telephone.class)
                .field("telephoneNumber").equal(telephoneNumber).asList();
        for (Telephone telephone: telephones) {
            names.add(telephone.getName());
        }
        return names;
    }

    /** Возвращает все телефоны, которые есть в базе данных.*/
    public List<Telephone> findAll() {
        return datastore.find(Telephone.class).asList();
    }

    /** У указанной пары "имя-телефон" меняет телефон.*/
    public void changeTelephoneNumber(String name, String oldTelephoneNumber, String newTelephoneNumber) {
        delete(name, oldTelephoneNumber);
        add(name, newTelephoneNumber);
    }

    /** У указанной пары "имя-телефон" меняет имя.*/
    public void changeName(String oldName, String newName, String telephoneNumber) {
        delete(oldName, telephoneNumber);
        add(newName, telephoneNumber);
    }

    /** Удаляет базу данных.*/
    public void clear() {
        datastore.getDB().dropDatabase();
    }

}
