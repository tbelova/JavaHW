package ru.spbau;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class NumbersTest {
    @Test
    public void readNumberTest1() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("110".getBytes());
        assertEquals(110, Numbers.readNumber(in).get());
    }

    @Test
    public void readNumberTest2() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("110l".getBytes());
        assertFalse(Numbers.readNumber(in).isPresent());
    }

    @Test
    public void toSqrtTest() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("1\n2\n3\n4\nabcd\n5.0\n6\n7\n10\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Numbers.toSqrt(in, out);
        assertEquals("1\n4\n9\n16\nnull\nnull\n36\n49\n100\n", out.toString());
    }

}