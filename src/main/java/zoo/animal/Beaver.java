package zoo.animal;

public record Beaver(String name) implements Rodent {
  public Beaver {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
