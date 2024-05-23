import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class NewMethodsArraysFeatureExample {

    private static final Comparator<Plane> MODEL_ONLY_PLANE_COMPARATOR =
            Comparator.comparing(p -> p.model);

    private static class Plane {
        private final String name;
        private final String model;

        public Plane(String name, String model) {
            this.name = name;
            this.model = model;
        }
    }

    public static void main(String[] args) {
        var array1 = new int[]{1, 2, 3, 4, 5, 6};
        //zero based index
        var copyRange = Arrays.copyOfRange(array1, 2, 4);
        System.out.println(Arrays.toString(copyRange));
        var planes1 = new Plane[]{new Plane("Plane 1", "A320"), new Plane("Plane 2", "B738")};
        var planes2 = new Plane[]{new Plane("Plane 3", "A320"), new Plane("Plane 4", "B738")};
        System.out.println(Arrays.equals(planes1, planes2, MODEL_ONLY_PLANE_COMPARATOR)); //true
    }
}
