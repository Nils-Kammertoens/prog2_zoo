package zoo.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import zoo.enclosure.Enclosure;
import zoo.result.Result;
import zoo.result.ZooError;

public class CommandManager<T> {
  private static final Logger LOG = Logger.getLogger(CommandManager.class.getName());

  private final Deque<Command<? super T, ZooError, String>> undoStack = new ArrayDeque<>();
  private final Deque<Command<? super T, ZooError, String>> redoStack = new ArrayDeque<>();

  public Result<ZooError, String> executeCommand(
      Command<? super T, ZooError, String> command, T target) {
    Objects.requireNonNull(command, "command must not be null");
    Objects.requireNonNull(target, "target must not be null");

    LOG.info(() -> "executeCommand(command=%s)".formatted(command.description()));
    Result<ZooError, String> result = command.execute(target);

    if (result instanceof Result.Ok<?, ?>) {
      undoStack.push(command);
      redoStack.clear();
    }

    logResult("executeCommand", command, result);
    logTargetState(target);
    return result;
  }

  public Result<ZooError, String> undo(T target) {
    Objects.requireNonNull(target, "target must not be null");
    LOG.info("undo()");

    if (undoStack.isEmpty()) {
      Result<ZooError, String> result = Result.err(ZooError.NO_UNDO_AVAILABLE);
      logResult("undo", null, result);
      logTargetState(target);
      return result;
    }

    Command<? super T, ZooError, String> command = undoStack.pop();
    Result<ZooError, String> result = command.undo(target);

    if (result instanceof Result.Ok<?, ?>) {
      redoStack.push(command);
    } else {
      undoStack.push(command);
    }

    logResult("undo", command, result);
    logTargetState(target);
    return result;
  }

  public Result<ZooError, String> redo(T target) {
    Objects.requireNonNull(target, "target must not be null");
    LOG.info("redo()");

    if (redoStack.isEmpty()) {
      Result<ZooError, String> result = Result.err(ZooError.NO_REDO_AVAILABLE);
      logResult("redo", null, result);
      logTargetState(target);
      return result;
    }

    Command<? super T, ZooError, String> command = redoStack.pop();
    Result<ZooError, String> result = command.execute(target);

    if (result instanceof Result.Ok<?, ?>) {
      undoStack.push(command);
    } else {
      redoStack.push(command);
    }

    logResult("redo", command, result);
    logTargetState(target);
    return result;
  }

  public int undoSize() {
    return undoStack.size();
  }

  public int redoSize() {
    return redoStack.size();
  }

  private void logResult(
      String operation,
      Command<? super T, ZooError, String> command,
      Result<ZooError, String> result) {
    String commandText = command == null ? "kein Kommando" : command.description();

    if (result instanceof Result.Ok<?, ?> ok) {
      LOG.info(() -> "%s erfolgreich: %s (%s)".formatted(operation, ok.value(), commandText));
      return;
    }

    if (result instanceof Result.Err<?, ?> err) {
      ZooError error = (ZooError) err.error();
      LOG.log(
          levelFor(error),
          () ->
              "%s nicht ausgeführt: %s (%s, %s)"
                  .formatted(operation, error.message(), error, commandText));
    }
  }

  private Level levelFor(ZooError error) {
    return switch (error) {
      case NO_UNDO_AVAILABLE, NO_REDO_AVAILABLE -> Level.INFO;
      case COMMAND_NOT_EXECUTED, ANIMAL_ALREADY_PRESENT, ANIMAL_NOT_FOUND -> Level.WARNING;
    };
  }

  private void logTargetState(T target) {
    LOG.fine(() -> "Zustand des Ziels: " + describeTarget(target));
  }

  private String describeTarget(T target) {
    if (target instanceof Enclosure<?> enclosure) {
      return "%s Bewohner=%s".formatted(enclosure, enclosure.getInhabitants());
    }
    return String.valueOf(target);
  }
}
