package ru.spbau;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import static org.junit.Assert.*;

public class SerializationTest {

    private static class Clazz {
        private int firstFiled;
        private int secondFiled;
        private String thirdField;
        private int forthFiled;
        private String fifthField;
        private String sixthField;

        public Clazz() {}

        public Clazz(int firstFiled, int secondFiled, String thirdField, int forthFiled,
                     String fifthField, String sixthField) {
            this.firstFiled = firstFiled;
            this.secondFiled = secondFiled;
            this.thirdField = thirdField;
            this.forthFiled = forthFiled;
            this.fifthField = fifthField;
            this.sixthField = sixthField;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Clazz) {
                Clazz clazz = (Clazz)object;
                return firstFiled == clazz.firstFiled &&
                       secondFiled == clazz.secondFiled &&
                       thirdField.equals(clazz.thirdField) &&
                       forthFiled == clazz.forthFiled &&
                       fifthField.equals(clazz.fifthField) &&
                       sixthField.equals(clazz.sixthField);
            }
            return false;
        }

    }

    @Test
    public void serializationAndDeserializationTest() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Clazz clazz = new Clazz(1, 2, "Heey \n hoooh", 4, "lol", "yay");

        Serialization.serialize(clazz, outputStream);
        Clazz res = Serialization.deserialize(new ByteArrayInputStream(outputStream.toByteArray()), Clazz.class);

        assertEquals(clazz, res);
    }

}