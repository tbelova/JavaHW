package ru.spbau;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static ru.spbau.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        ArrayList<String> paths = new ArrayList<>();
        paths.add("findQuotesTest/file1");
        paths.add("findQuotesTest/file2");
        paths.add("findQuotesTest/file3");

        ArrayList<String> res = new ArrayList<>();
        res.add("kmcajishkvml;v mmfdkljlkjl kamamasfddgg");
        res.add("kllfdjkldjv,ss,s,a,asf  o opkfdlv  sdgfmamafj klj");
        res.add("awqewmamaiieruifsfkljsd");
        res.add("sddmama ");
        res.add("gdvxmkJKHkjhjkHmamaKjkHJKHKJJKhkkjdflklcz';dlw");
        res.add("t;'flgj;lmamabm D");
        res.add(" r,g ldklklmamasejf");

        assertEquals(res, findQuotes(paths, "mama"));
    }

    @Test
    public void testPiDividedBy4() {
        assertTrue(Math.abs(Math.PI / (double)4 - piDividedBy4()) < 1e-2);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> book = new HashMap<>();
        ArrayList<String> pushkin = new ArrayList<>();
        pushkin.add("U lukomorya dub zelenyi..");
        pushkin.add("Uzh blizok polden', zhar pylaet..");

        ArrayList<String> dostoevcky = new ArrayList<>();
        dostoevcky.add("Tvar' ly ya drozach'aya, ili pravo imeu..");

        ArrayList<String> tolstoy = new ArrayList<>();
        tolstoy.add("Ezheli by vse voevali so svoimi ybezhdeniyami..");

        book.put("Pushkin", pushkin);
        book.put("Dostoevcky", dostoevcky);
        book.put("Tolstoy", tolstoy);

        assertEquals("", findPrinter(new HashMap<>()));
        assertEquals("Pushkin", findPrinter(book));
    }

    @Test
    public void testCalculateGlobalOrder() {
        HashMap<String, Integer> firstOrder = new HashMap<>();

        firstOrder.put("Potato", 5);
        firstOrder.put("Carrot", 10);
        firstOrder.put("Candy", 125);
        firstOrder.put("Water", 12);

        HashMap<String, Integer> secondOrder = new HashMap<>();

        secondOrder.put("Potato", 15);
        secondOrder.put("Carrot", 2);
        secondOrder.put("Candy", 317);
        secondOrder.put("Limonade", 19);

        HashMap<String, Integer> thirdOrder = new HashMap<>();

        thirdOrder.put("Candy", 100);
        thirdOrder.put("Limonade", 30);

        HashMap<String, Integer> result = new HashMap<>();

        result.put("Potato", 20);
        result.put("Carrot", 12);
        result.put("Candy", 542);
        result.put("Water", 12);
        result.put("Limonade", 49);

        ArrayList<Map<String, Integer>> orders = new ArrayList<>();
        orders.add(firstOrder);
        orders.add(secondOrder);
        orders.add(thirdOrder);

        assertEquals(result, calculateGlobalOrder(orders));

    }
}
