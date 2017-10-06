package lambda.part2.example;

import data.Person;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("UnnecessaryLocalVariable")
public class FunctionCombination {

    @Test
    public void personToInt0() {
        // Person -> Integer
        Function<Person, Integer> lastNameLength = p -> p.getLastName().length();

        assertEquals(5, lastNameLength.apply(new Person("a", "abcde", 0)).intValue());
    }

    // A -> B
    // B -> C
    // A -> C

    // (Person -> String, String -> Integer) -> (Person -> Integer)
    private Function<Person, Integer> personStringPropertyToInt(
            Function<Person, String> personToString,
            Function<String, Integer> stringToInteger) {
        return person -> stringToInteger.apply(personToString.apply(person));
    }

    @Test
    public void personToInt1() {
        Function<Person, String> getLastName = Person::getLastName;
        Function<String, Integer> getLength = String::length;
        Function<Person, Integer> lastNameLength = personStringPropertyToInt(getLastName, getLength);

        assertEquals(5, lastNameLength.apply(new Person("a", "abcde", 0)).intValue());
    }

    // (A -> B, B -> C) -> (A -> C)
    private <A, B, C> Function<A, C> andThen(Function<A, B> f1, Function<B, C> f2) {
        return a -> f2.apply(f1.apply(a));
    }

    @Test
    public void personToInt2() {
        Function<Person, String> getLastName = Person::getLastName;
        Function<String, Integer> getLength = String::length;
        Function<Person, Integer> lastNameLength = andThen(getLastName, getLength);

        assertEquals(5, lastNameLength.apply(new Person("a", "abcde", 0)).intValue());
    }


    static class MyClass {

    }

    static class InheritedMyClass extends MyClass {

    }

    @Test
    public void personToInt3() {
        Function<Person, String> getLastName = Person::getLastName;
        Function<String, Integer> getLength = String::length;
        Function<Person, Integer> lastNameLength = getLastName.andThen(getLength);

        MyClass ref = new InheritedMyClass();
        Class<? extends MyClass> myClassClazz = ref.getClass();


        Class<? extends Integer> integerClazz = Integer.valueOf(10).getClass();

        Class<MyClass> reallyIntegerClazz = MyClass.class;


        List<String> list = new ArrayList<>();
        List<Integer> listIntegers = new ArrayList<>();
        list.add("1");

        String val = (String)list.get(0);


        List rawReference = list;
        rawReference.add(new MyClass());

        System.out.println(list.size());

        List<String> hardCheckedList = Collections.checkedList(list, String.class);

        List rawReference2 = hardCheckedList;
//        rawReference2.add(new MyClass());  <- Упадем с ClassCastException

        System.out.println(list.size());

//        hardCheckedList

        for (Method method : Person.class.getDeclaredMethods()) {
            System.out.println(method);
        }

        try {
            Method compareToPerson = Person.class.getMethod("compareTo", Person.class);
            Method compareToObject = Person.class.getMethod("compareTo", Object.class);

            System.out.println(compareToPerson.isSynthetic());
            System.out.println(compareToObject.isSynthetic());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // Person.compareTo both methods has same erasure

        // GenericException

//        List<String>[] arrayOfLists = new List<String>[10];
        Integer[] integers = new Integer[10];
        integers[0] = 100;



        Number[] numbers = integers;
//        numbers[1] = 200d; <- ArrayStoreException


        Number numVal = 10;

//        List<Object> objectList = new ArrayList<String>();
        List<? extends Number> listNumbers = new ArrayList<Double>();
//        Number numberVal = listNumbers.get(0);

        listNumbers.add(null);

        while (true) {
            try {
                Integer a = new Integer(300);
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (false) {
                break;
            }
        }


        // Array generics
        // Raw types list (Int -> Double)

        assertEquals(5, lastNameLength.apply(new Person("a", "abcde", 0)).intValue());
    }


    class MyException extends RuntimeException {

    }
}
