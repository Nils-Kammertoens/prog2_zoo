package zoo.command;

import zoo.result.Result;

public interface Command<T, E, R> {
  Result<E, R> execute(T target);

  Result<E, R> undo(T target);

  String description();
}
