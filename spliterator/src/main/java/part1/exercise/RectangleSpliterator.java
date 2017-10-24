package part1.exercise;

import part1.example.IntArraySpliterator;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;

public class RectangleSpliterator extends Spliterators.AbstractIntSpliterator {

    private final int[][] array;
    private int positionX;
    private int positionY;
    private int arrayLengthX;
    private int arrayLengthY;

    public RectangleSpliterator(int[][] array) {
        super(checkArrayAndCalcEstimatedSize(array), Spliterator.IMMUTABLE
                | Spliterator.ORDERED
                | Spliterator.SIZED
                | Spliterator.SUBSIZED
                | Spliterator.NONNULL);      // TODO заменить
//       super(estimatedSize, Spliterator.IMMUTABLE
//                          | Spliterator.ORDERED
//                          | Spliterator.SIZED
//                          | Spliterator.SUBSIZED
//                          | Spliterator.NONNULL);
        this.array = array;
        positionX = 0;
        positionY = 0;
        arrayLengthX = array.length;
        arrayLengthY = array[0].length;
    }

    public RectangleSpliterator(int[][] array, int positionX, int positionY, int arrayLengthX, int arrayLengthY) {
        super(checkArrayAndCalcEstimatedSize(array), 0);
        this.array = array;
        this.arrayLengthX = arrayLengthX;
        this.arrayLengthY = arrayLengthY;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    private static long checkArrayAndCalcEstimatedSize(int[][] array) {
        //TODO
        long size = 0;
        for (int i = 0; i < array.length; i++) {
            size += array[i].length;
        }
        return size;
    }

    //Принимает двумерный массив. Задача - развернуть матрицу и предоставить возможность обрабатывать в потоке.
    //попроще - trySplit - по строкам
    @Override
    public OfInt trySplit() {
        // TODO
        int midX;
        int midY;
        RectangleSpliterator result;
        if (positionX == arrayLengthX) {
            return null;
        }
        if ((arrayLengthX - positionX) % 2 == 0) {
            midX = positionX + (arrayLengthX - positionX) / 2;
            result = new RectangleSpliterator(array, positionX, positionY, midX, arrayLengthY);
        } else {
            midX = positionX + (arrayLengthX - positionX) / 2 + 1;
            midY = positionY + (arrayLengthY - positionY) / 2;
            result = new RectangleSpliterator(array, positionX, positionY, midX, midY);
            positionY = midY;
        }
        positionX = midX;
        return result;
    }

    @Override
    public long estimateSize() {
        // TODO
        long size = 0;
        for (int i = positionX; i < arrayLengthX; i++) {
            size += array[i].length;
        }
        if (positionY != 0) {
            size += positionY;
        }
        return size;
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        // TODO
        if (positionX < arrayLengthX - 1 || (positionX == arrayLengthX - 1 && positionY < arrayLengthY)) {
            int value = array[positionX][positionY];
            positionY++;
            if (positionY == array[positionX].length) {
                positionY = 0;
                positionX++;
            }
            action.accept(value);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] array = new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        RectangleSpliterator spliterator = new RectangleSpliterator(array);

        spliterator.tryAdvance((int value) -> System.out.println(value));
        System.out.println("All stream");
        spliterator.trySplit().trySplit().forEachRemaining((IntConsumer) System.out::println);
    }
}


class A {

    protected String val;

    A() {
        setVal();
    }

    public void setVal() {
        val = "A";
    }
}

class B extends A {

    @Override
    public void setVal() {
        val = "B";
    }

    public static void main(String[] args) {
        System.out.println(new B().val);

    }
}