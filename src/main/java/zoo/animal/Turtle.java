package zoo.animal;

public record Turtle(String name) implements Reptile {
  public Turtle {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
