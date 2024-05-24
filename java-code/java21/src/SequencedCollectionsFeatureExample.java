import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequencedCollectionsFeatureExample {
    public static void main(String[] args) {
        ArrayList<Integer> arrayList = new ArrayList<>();

        arrayList.add(1);   // List contains: [1]

        arrayList.addFirst(0);  // List contains: [0, 1]
        arrayList.addLast(2);   // List contains: [0, 1, 2]

        Integer firstElement = arrayList.getFirst();  // 0
        Integer lastElement = arrayList.getLast();  // 2

        List<Integer> reversed = arrayList.reversed();
        System.out.println(reversed); // Prints [2, 1, 0]
    }
}
