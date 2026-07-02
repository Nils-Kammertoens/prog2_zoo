package zoo.animal;

public record Snake(String name) implements Reptile {
  public Snake {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
