package part1.exercise;

import part1.example.IntArraySpliterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Spliterators;
import java.util.function.IntConsumer;

public class RectangleSpliterator extends Spliterators.AbstractIntSpliterator {

    private final int[][] array;
    private int startInclusive;
    private long endExclusive;
    private int arrayLengthX;
    private int arrayLengthY;

    public RectangleSpliterator(int[][] array) {
        super(checkArrayAndCalcEstimatedSize(array), 0);       // TODO заменить
//       super(estimatedSize, Spliterator.IMMUTABLE
//                          | Spliterator.ORDERED
//                          | Spliterator.SIZED
//                          | Spliterator.SUBSIZED
//                          | Spliterator.NONNULL);
        this.array = array;
        startInclusive = 0;
        endExclusive = checkArrayAndCalcEstimatedSize(array);
        arrayLengthX = array.length;
        arrayLengthY = array[0].length;
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
        //throw new UnsupportedOperationException();
        int mid;
        ArrayList<Integer> resultList = new ArrayList<>();
        int[] resultArray = new int[(int)endExclusive];

        if (arrayLengthX % 2 == 0) {
            mid = arrayLengthX / 2 * arrayLengthY - 1;
            for (int i = 0; i < arrayLengthX / 2; i++) {
                for (int j = 0; j < arrayLengthY; j++) {
                    resultArray[i * arrayLengthY + j] = array[i][j];
                    resultList.add(array[i][j]);
                }
            }
        } else {
            mid = arrayLengthX / 2 * arrayLengthY + arrayLengthY / 2;
            for (int i = 0; i < arrayLengthX / 2 + 1; i++) {
                if (i != arrayLengthX / 2) {
                    for (int j = 0; j < arrayLengthY; j++) {
                        resultArray[i * arrayLengthY + j] = array[i][j];
                        resultList.add(array[i][j]);
                    }
                } else {
                    for (int j = 0; j < arrayLengthY / 2; j++) {
                        resultArray[i * arrayLengthY + j] = array[i][j];
                    }
                }
            }
        }
        IntArraySpliterator result = new IntArraySpliterator(resultArray);
        startInclusive = mid;
        return result;
    }

    @Override
    public long estimateSize() {
        // TODO
        //throw new UnsupportedOperationException();
        return endExclusive - startInclusive;
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        // TODO
        //throw new UnsupportedOperationException();
        if (startInclusive < endExclusive) {
            int value = array[startInclusive / arrayLengthY][startInclusive % arrayLengthY];
            startInclusive += 1;
            action.accept(value);
            return true;
        }
        return false;
    }
}
