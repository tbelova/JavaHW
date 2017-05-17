package ru.spbau;

import static org.junit.Assert.assertEquals;

public class SimpleTestClassWithBeforeAndAfter {

    private int a = 0;

    @Before
    public void before() throws Exception {
        assertEquals(0, a);
        a = 1;
    }

    @Test
    public void testOk() throws Exception {
        assertEquals(1, a);
        a = 0;
    }

    @Test
    public void testFail() throws Exception {
        assertEquals(0, a);
        a = 0;
    }

    @Test
    public void testFailInAfter() throws Exception {
        assertEquals(1, a);
        a = 2;
    }

    @After
    public void after() throws Exception {
        assertEquals(0, a);
    }

}
