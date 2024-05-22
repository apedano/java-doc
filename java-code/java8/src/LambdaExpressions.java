import java.util.function.Function;

public class LambdaExpressions {

    private static final Function<Long, String> valueOfLambda = s -> {
        System.out.println("The input is " + s);
        return s.toString();
    };

    private static final Function<Long, String> valueOfMethodReference = Object::toString;

    public static void main(String[] args) {
        System.out.println(LambdaExpressions.valueOfLambda.apply(123L));
        System.out.println(LambdaExpressions.valueOfMethodReference.apply(456L));
    }
}
