package zoo.result;

public sealed interface Result<E, R> permits Result.Ok, Result.Err {
  record Ok<E, R>(R value) implements Result<E, R> {}

  record Err<E, R>(E error) implements Result<E, R> {}

  static <E, R> Result<E, R> ok(R value) {
    return new Ok<>(value);
  }

  static <E, R> Result<E, R> err(E error) {
    return new Err<>(error);
  }

  default boolean isOk() {
    return this instanceof Ok<?, ?>;
  }
}
