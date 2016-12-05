package ru.spbau;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private Morphia morphia;
    private Datastore datastore;

    public DataBase(String dataBaseName) {
        morphia = new Morphia();
        morphia.mapPackage("ru.spbau");
        datastore = morphia.createDatastore(new MongoClient(), dataBaseName);
    }

    public void add(String name, String telephoneNumber) {
        Telephone telephone = new Telephone(name, telephoneNumber);
        datastore.save(telephone);
    }

    public void delete(String name, String telephoneNumber) {
        datastore.delete(Telephone.class, name + telephoneNumber);
    }

    public List<String> findTelephonesByName(String name) {
        List<String> telephoneNumbers = new ArrayList<>();
        List<Telephone> telephones =  datastore.createQuery(Telephone.class)
                .field("name").equal(name).asList();
        for (Telephone telephone: telephones) {
            telephoneNumbers.add(telephone.getTelephoneNumber());
        }
        return telephoneNumbers;
    }

    public List<String> findNamesByTelephone(String telephoneNumber) {
        List<String> names = new ArrayList<>();
        List<Telephone> telephones =  datastore.createQuery(Telephone.class)
                .field("telephoneNumber").equal(telephoneNumber).asList();
        for (Telephone telephone: telephones) {
            names.add(telephone.getName());
        }
        return names;
    }

    public List<Telephone> findAll() {
        return datastore.find(Telephone.class).asList();
    }

    public void changeTelephoneNumber(String name, String oldTelephoneNumber, String newTelephoneNumber) {
        delete(name, oldTelephoneNumber);
        add(name, newTelephoneNumber);
    }

    public void changeName(String oldName, String newName, String telephoneNumber) {
        delete(oldName, telephoneNumber);
        add(newName, telephoneNumber);
    }

    public void clear() {
        datastore.getDB().dropDatabase();
    }

}
