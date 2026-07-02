package zoo.animal;

public record Mouse(String name) implements Rodent {
  public Mouse {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
