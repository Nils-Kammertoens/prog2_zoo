package zoo.enclosure;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import zoo.animal.Animal;

public class Enclosure<T extends Animal> {
  private final String name;
  private final Set<T> inhabitants = new LinkedHashSet<>();

  public Enclosure(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean add(T animal) {
    return inhabitants.add(Objects.requireNonNull(animal, "animal must not be null"));
  }

  public boolean remove(T animal) {
    return inhabitants.remove(Objects.requireNonNull(animal, "animal must not be null"));
  }

  public boolean removeAnimal(Animal animal) {
    return inhabitants.remove(Objects.requireNonNull(animal, "animal must not be null"));
  }

  public boolean contains(Animal animal) {
    return inhabitants.contains(Objects.requireNonNull(animal, "animal must not be null"));
  }

  public Optional<T> findAnimalByName(String animalName) {
    Objects.requireNonNull(animalName, "animalName must not be null");
    return inhabitants.stream().filter(animal -> animal.name().equals(animalName)).findFirst();
  }

  public int size() {
    return inhabitants.size();
  }

  public List<T> getInhabitants() {
    return List.copyOf(inhabitants);
  }

  @Override
  public String toString() {
    return "%s{name='%s', inhabitants=%d}"
        .formatted(getClass().getSimpleName(), name, inhabitants.size());
  }
}
