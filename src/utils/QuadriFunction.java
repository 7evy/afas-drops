package utils;

@FunctionalInterface
public interface QuadriFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);
}
