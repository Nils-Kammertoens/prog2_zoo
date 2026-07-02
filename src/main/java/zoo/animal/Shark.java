package zoo.animal;

public record Shark(String name) implements Fish {
  public Shark {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
