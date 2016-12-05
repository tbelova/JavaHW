package ru.spbau;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataBaseTest {

    DataBase dataBase;

    @Before
    public void init() {
        dataBase = new DataBase("TelephoneNumbers");
    }

    @Test
    public void addTest() throws Exception {
        dataBase.add("Vasya", "89876543210");
    }

    @Test
    public void deleteTest() throws Exception {
        dataBase.add("Vasya", "89876543210");
        dataBase.add("Vasya", "+79876543210");
        dataBase.delete("Vasiliy", "89876543210");
    }

    @Test
    public void findTelephonesByNameTest() throws Exception {
        dataBase.add("1", "1");
        dataBase.add("1", "12");
        dataBase.add("1", "123");
        dataBase.add("1", "1234");
        dataBase.add("1", "12345");
        dataBase.add("2", "1");
        List<String> telephones = dataBase.findTelephonesByName("1");
        List<String> ans = new ArrayList<>();
        ans.add("1");
        ans.add("12");
        ans.add("123");
        ans.add("1234");
        ans.add("12345");
        assertEquals(ans, telephones);
    }

    @Test
    public void findNamesByTelephoneTest() throws Exception {
        dataBase.add("1", "1");
        dataBase.add("1", "12345");
        dataBase.add("2", "12345");
        dataBase.add("3", "12345");
        dataBase.add("4", "12345");
        List<String> names = dataBase.findNamesByTelephone("12345");
        List<String> ans = new ArrayList<>();
        ans.add("1");
        ans.add("2");
        ans.add("3");
        ans.add("4");
        assertEquals(ans, names);
    }

    @Test
    public void findAllTest() throws Exception {
        dataBase.add("1", "1-1");
        dataBase.add("2", "2-1");
        dataBase.add("2", "2-2");
        dataBase.add("3", "3-1");
        dataBase.add("3", "2-1");
        List<Telephone> telephones = dataBase.findAll();
        List<Telephone> ans = new ArrayList<>();
        ans.add(new Telephone("1", "1-1"));
        ans.add(new Telephone("2", "2-1"));
        ans.add(new Telephone("2", "2-2"));
        ans.add(new Telephone("3", "3-1"));
        ans.add(new Telephone("3", "2-1"));
        telephones.sort(Telephone::compareTo);
        ans.sort(Telephone::compareTo);
        assertEquals(ans.size(), telephones.size());
        for (int i = 0; i < ans.size(); i++) {
            assertEquals(ans.get(i), telephones.get(i));
        }
    }

    @Test
    public void changeNameTest() throws Exception {
        dataBase.add("Vasya", "89876543210");
        dataBase.changeName("Vasya", "Petya", "89876543210");
        List<Telephone> telephones = dataBase.findAll();
        assertEquals(1, telephones.size());
        assertEquals(new Telephone("Petya", "89876543210"), telephones.get(0));
    }

    @Test
    public void changeTelephoneTest() throws Exception {
        dataBase.add("Vasya", "89876543210");
        dataBase.changeTelephoneNumber("Vasya", "89876543210", "12345");
        List<Telephone> telephones = dataBase.findAll();
        assertEquals(1, telephones.size());
        assertEquals(new Telephone("Vasya", "12345"), telephones.get(0));
    }

    @After
    public void clear() {
        dataBase.clear();
    }

}