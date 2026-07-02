package zoo.animal;

public record Trout(String name) implements Fish {
  public Trout {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
