public class RecordPatternFeatureExample {
    record Point(int x, int y){}

    //As of Java 16
    static void printPoint(Object obj) {
        if (obj instanceof Point p) {
            int x = p.x();
            int y = p.y();
            System.out.println(x+y);
        }
    }

    // As of Java 21 we can extract the x and y components from the Point value directly,
    // invoking the accessor methods on our behalf.
    static void printSum(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            System.out.println(x+y);
        }
    }
}
