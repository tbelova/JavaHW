package ru.spbau;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DataBase dataBase = new DataBase("TelephoneNumbers");

        Scanner scanner = new Scanner(System.in);
        boolean ok = true;

        String name;
        String telephoneNumber;

        while (ok) {
            int operation = scanner.nextInt();
            switch (operation) {
                case 0:
                    ok = false;
                    break;
                case 1:
                    name = scanner.next();
                    telephoneNumber = scanner.next();
                    dataBase.add(name, telephoneNumber);
                    break;
                case 2:
                    name = scanner.next();
                    List<String> telephoneNumbers = dataBase.findTelephonesByName(name);
                    for (String telephone: telephoneNumbers) {
                        System.out.println(telephone);
                    }
                    break;
                case 3:
                    telephoneNumber = scanner.next();
                    List<String> names = dataBase.findNamesByTelephone(telephoneNumber);
                    for (String telephoneName: names) {
                        System.out.println(telephoneName);
                    }
                    break;
                case 4:
                    name = scanner.next();
                    telephoneNumber = scanner.next();
                    dataBase.delete(name, telephoneNumber);
                    break;
                case 5:
                    name = scanner.next();
                    telephoneNumber = scanner.next();
                    String newName = scanner.next();
                    dataBase.changeName(name, newName, telephoneNumber);
                    break;
                case 6:
                    name = scanner.next();
                    telephoneNumber = scanner.next();
                    String newTelephoneNumber = scanner.next();
                    dataBase.changeTelephoneNumber(name, telephoneNumber, newTelephoneNumber);
                    break;
                case 7:
                    List<Telephone> allTelephones = dataBase.findAll();
                    for (Telephone phone: allTelephones) {
                        phone.print();
                    }
                    break;
            }
        }
    }

}
