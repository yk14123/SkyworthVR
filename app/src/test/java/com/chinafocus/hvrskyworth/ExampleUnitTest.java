package com.chinafocus.hvrskyworth;


import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.TreeSet;


import static org.junit.Assert.*;

/**
 * Example local unit sky_worth_login_logo, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testClone() {
//        Person person = new Person();
//        TestStack testStack = new TestStack(person);
//
////        System.out.println(testStack);
//
////        System.out.println(person);
//        System.out.println(testStack.objects[0]);
//
////        System.out.println(testStack.objects);
//
//        try {
//            TestStack clone = (TestStack) testStack.clone();
//
////            System.out.println(clone.objects);
//            System.out.println(clone.objects[0]);
////            System.out.println(clone);
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void testSort() {
//        List<Integer> list = new ArrayList<>();
//        list.add(3);
//        list.add(1);
//        list.add(2);
//
//        Collections.sort(list);
//
//        System.out.println(list);
//
//        int[] arr = {3, 1, 2};
//
//        Arrays.sort(arr);
//
//        System.out.println(Arrays.toString(arr));

        BigDecimal b1 = new BigDecimal("1.0");
        BigDecimal b2 = new BigDecimal("1.00");

        int i = b1.compareTo(b2);
        System.out.println(i);

        HashSet<BigDecimal> hashSet = new HashSet<>();
        hashSet.add(b1);
        hashSet.add(b2);

        System.out.println(hashSet.size());

        TreeSet<BigDecimal> treeSet = new TreeSet<>(hashSet);

        System.out.println(treeSet.size());
    }

    @Test
    public void testComp() {
//        Compa<Person> personCompa = ((Compa<Person>) (t1, t2) -> t1.age - t2.age).thenComp((t1, t2) -> t1.sex - t2.sex);
//        Person p1 = new Person();
//        p1.age = 10;
//        p1.sex = 1;
//        Person p2 = new Person();
//        p2.age = 10;
//        p2.sex = 1;

    }
}