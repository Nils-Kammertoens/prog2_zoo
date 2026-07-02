package zoo.animal;

public record Chimpanzee(String name) implements Primate {
  public Chimpanzee {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }
}
