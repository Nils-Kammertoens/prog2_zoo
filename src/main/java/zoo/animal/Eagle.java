package zoo.animal;

public record Eagle(String name) implements Bird {
  public Eagle {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
