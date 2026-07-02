package zoo.animal;

public record Elephant(String name) implements Mammal {
  public Elephant {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
