package zoo.animal;

public record Giraffe(String name) implements Mammal {
  public Giraffe {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
