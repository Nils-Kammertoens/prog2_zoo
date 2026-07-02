package zoo.animal;

public record Penguin(String name) implements Bird {
  public Penguin {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
