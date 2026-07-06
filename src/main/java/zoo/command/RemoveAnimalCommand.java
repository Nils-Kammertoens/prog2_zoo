package zoo.command;

import java.util.Objects;
import zoo.animal.Animal;
import zoo.enclosure.Enclosure;
import zoo.result.Result;
import zoo.result.ZooError;

public class RemoveAnimalCommand<T extends Animal>
    implements Command<Enclosure<? super T>, ZooError, String> {
  private final T animal;
  private boolean executed;

  public RemoveAnimalCommand(T animal) {
    this.animal = Objects.requireNonNull(animal, "animal must not be null");
  }

  @Override
  public Result<ZooError, String> execute(Enclosure<? super T> target) {
    Objects.requireNonNull(target, "target must not be null");

    if (!target.remove(animal)) {
      return Result.err(ZooError.ANIMAL_NOT_FOUND);
    }

    executed = true;
    return Result.ok(
        "Tier '%s' wurde aus '%s' entnommen.".formatted(animal.name(), target.getName()));
  }

  @Override
  public Result<ZooError, String> undo(Enclosure<? super T> target) {
    Objects.requireNonNull(target, "target must not be null");

    if (!executed) {
      return Result.err(ZooError.COMMAND_NOT_EXECUTED);
    }

    if (!target.add(animal)) {
      return Result.err(ZooError.ANIMAL_ALREADY_PRESENT);
    }

    executed = false;
    return Result.ok(
        "Entnahme von Tier '%s' aus '%s' wurde rückgängig gemacht."
            .formatted(animal.name(), target.getName()));
  }

  @Override
  public String description() {
    return "RemoveAnimalCommand[%s]".formatted(animal.name());
  }

  public T animal() {
    return animal;
  }
}
