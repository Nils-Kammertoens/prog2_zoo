package zoo.animal;

public record Lion(String name) implements Cat {
  public Lion {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
