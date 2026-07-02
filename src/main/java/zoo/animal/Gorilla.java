package zoo.animal;

public record Gorilla(String name) implements Primate {
  public Gorilla {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
