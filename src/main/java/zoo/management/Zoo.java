package zoo.management;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import zoo.animal.Animal;
import zoo.animal.Bird;
import zoo.animal.Fish;
import zoo.animal.Mammal;
import zoo.animal.Reptile;
import zoo.enclosure.Enclosure;

public class Zoo {
  private static final Logger LOG = Logger.getLogger(Zoo.class.getName());

  private final List<Enclosure<? extends Animal>> enclosures = new ArrayList<>();

  public boolean addEnclosure(Enclosure<? extends Animal> enclosure) {
    LOG.info(() -> "addEnclosure(enclosure=%s)".formatted(enclosureName(enclosure)));
    Objects.requireNonNull(enclosure, "enclosure must not be null");

    boolean nameAlreadyExists =
        enclosures.stream().anyMatch(existing -> existing.getName().equals(enclosure.getName()));

    if (nameAlreadyExists) {
      LOG.warning(
          () -> "Gehege mit dem Namen '%s' existiert bereits.".formatted(enclosure.getName()));
      return false;
    }

    enclosures.add(enclosure);
    logFineState("addEnclosure");
    validateConsistency();
    return true;
  }

  public List<Enclosure<? extends Animal>> getEnclosures() {
    LOG.info("getEnclosures()");
    List<Enclosure<? extends Animal>> result = List.copyOf(enclosures);
    logFineState("getEnclosures");
    return result;
  }

  public Enclosure<? extends Animal> findEnclosureByName(String name) {
    LOG.info(() -> "findEnclosureByName(name=%s)".formatted(name));
    Objects.requireNonNull(name, "name must not be null");

    Enclosure<? extends Animal> result =
        enclosures.stream()
            .filter(enclosure -> enclosure.getName().equals(name))
            .findFirst()
            .orElse(null);

    if (result == null) {
      LOG.warning(() -> "Kein Gehege mit dem Namen '%s' gefunden.".formatted(name));
    } else {
      logFineState("findEnclosureByName");
    }

    return result;
  }

  public Optional<Animal> findAnimalByName(String animalName) {
    LOG.info(() -> "findAnimalByName(animalName=%s)".formatted(animalName));
    Objects.requireNonNull(animalName, "animalName must not be null");

    Optional<Animal> result =
        enclosures.stream()
            .map(enclosure -> enclosure.findAnimalByName(animalName))
            .flatMap(Optional::stream)
            .map(Animal.class::cast)
            .findFirst();

    if (result.isEmpty()) {
      LOG.warning(() -> "Kein Tier mit dem Namen '%s' gefunden.".formatted(animalName));
    } else {
      LOG.fine(() -> "Gefundenes Tier: " + result.orElseThrow());
    }

    return result;
  }

  public List<Animal> getAllAnimals() {
    LOG.info("getAllAnimals()");
    List<Animal> result =
        enclosures.stream()
            .flatMap(enclosure -> enclosure.getInhabitants().stream())
            .map(Animal.class::cast)
            .toList();
    logFineState("getAllAnimals");
    return result;
  }

  public List<Mammal> getAllMammals() {
    LOG.info("getAllMammals()");
    List<Mammal> result =
        enclosures.stream()
            .flatMap(enclosure -> enclosure.getInhabitants().stream())
            .filter(Mammal.class::isInstance)
            .map(Mammal.class::cast)
            .toList();
    logFineState("getAllMammals");
    return result;
  }

  public List<Animal> getAnimalsByPredicate(Predicate<Animal> predicate) {
    LOG.info(() -> "getAnimalsByPredicate(predicate=%s)".formatted(predicate));
    Objects.requireNonNull(predicate, "predicate must not be null");

    List<Animal> result = getAllAnimalsWithoutLogging().stream().filter(predicate).toList();
    logFineState("getAnimalsByPredicate");
    return result;
  }

  public Map<Class<? extends Animal>, Long> countAnimalsByType() {
    LOG.info("countAnimalsByType()");
    Map<Class<? extends Animal>, Long> result =
        getAllAnimalsWithoutLogging().stream()
            .collect(
                Collectors.groupingBy(
                    animal -> animal.getClass().asSubclass(Animal.class),
                    LinkedHashMap::new,
                    Collectors.counting()));
    logFineState("countAnimalsByType");
    return result;
  }

  public List<Enclosure<? extends Animal>> getOvercrowdedEnclosures(int maxAnimals) {
    LOG.info(() -> "getOvercrowdedEnclosures(maxAnimals=%d)".formatted(maxAnimals));
    if (maxAnimals < 0) {
      LOG.log(Level.SEVERE, "maxAnimals darf nicht negativ sein: {0}", maxAnimals);
      throw new IllegalArgumentException("maxAnimals must not be negative");
    }

    List<Enclosure<? extends Animal>> result =
        enclosures.stream().filter(enclosure -> enclosure.size() > maxAnimals).toList();
    logFineState("getOvercrowdedEnclosures");
    return result;
  }

  public String summary() {
    LOG.info("summary()");
    String result = buildSummaryText();
    LOG.fine(() -> "summary result: " + result);
    return result;
  }

  public <T extends Animal> boolean admitAnimal(Enclosure<T> enclosure, T animal) {
    LOG.info(
        () -> "admitAnimal(enclosure=%s, animal=%s)".formatted(enclosureName(enclosure), animal));
    Objects.requireNonNull(enclosure, "enclosure must not be null");
    Objects.requireNonNull(animal, "animal must not be null");

    if (!enclosures.contains(enclosure)) {
      LOG.warning(
          () ->
              "Tier kann nicht aufgenommen werden, weil das Gehege '%s' nicht im Zoo verwaltet wird."
                  .formatted(enclosure.getName()));
      return false;
    }

    if (containsAnimal(animal)) {
      LOG.warning(
          () -> "Tier '%s' befindet sich bereits in einem Gehege.".formatted(animal.name()));
      return false;
    }

    boolean added = enclosure.add(animal);
    if (!added) {
      LOG.warning(
          () ->
              "Tier '%s' konnte nicht in Gehege '%s' aufgenommen werden."
                  .formatted(animal.name(), enclosure.getName()));
      return false;
    }

    logFineState("admitAnimal");
    validateConsistency();
    return true;
  }

  public boolean releaseAnimal(Animal animal) {
    LOG.info(() -> "releaseAnimal(animal=%s)".formatted(animal));
    Objects.requireNonNull(animal, "animal must not be null");

    Enclosure<? extends Animal> current = findCurrentEnclosure(animal);
    if (current == null) {
      LOG.warning(() -> "Tier '%s' wurde in keinem Gehege gefunden.".formatted(animal.name()));
      return false;
    }

    boolean removed = current.removeAnimal(animal);
    if (!removed) {
      LOG.severe(
          () ->
              "Inkonsistenz: Tier '%s' wurde gefunden, konnte aber nicht entfernt werden."
                  .formatted(animal.name()));
      return false;
    }

    logFineState("releaseAnimal");
    validateConsistency();
    return true;
  }

  public <T extends Animal> boolean moveAnimal(T animal, Enclosure<T> target) {
    LOG.info(() -> "moveAnimal(animal=%s, target=%s)".formatted(animal, enclosureName(target)));
    Objects.requireNonNull(animal, "animal must not be null");
    Objects.requireNonNull(target, "target must not be null");

    if (!enclosures.contains(target)) {
      LOG.warning(
          () ->
              "Tier kann nicht umgesetzt werden, weil Zielgehege '%s' nicht im Zoo verwaltet wird."
                  .formatted(target.getName()));
      return false;
    }

    Enclosure<? extends Animal> current = findCurrentEnclosure(animal);
    if (current == null) {
      LOG.warning(() -> "Tier '%s' wurde in keinem Gehege gefunden.".formatted(animal.name()));
      return false;
    }

    if (current == target) {
      LOG.warning(
          () ->
              "Tier '%s' befindet sich bereits im Zielgehege '%s'."
                  .formatted(animal.name(), target.getName()));
      return false;
    }

    if (target.contains(animal)) {
      LOG.warning(
          () ->
              "Zielgehege '%s' enthält Tier '%s' bereits."
                  .formatted(target.getName(), animal.name()));
      return false;
    }

    boolean removed = current.removeAnimal(animal);
    if (!removed) {
      LOG.severe(
          () ->
              "Inkonsistenz: Tier '%s' wurde gefunden, konnte aber vor dem Umsetzen nicht entfernt werden."
                  .formatted(animal.name()));
      return false;
    }

    boolean added = target.add(animal);
    if (!added) {
      LOG.severe(
          () ->
              "Inkonsistenz: Tier '%s' konnte nach Entfernen nicht in Zielgehege '%s' eingefügt werden."
                  .formatted(animal.name(), target.getName()));
      return false;
    }

    logFineState("moveAnimal");
    validateConsistency();
    return true;
  }

  private boolean containsAnimal(Animal animal) {
    return enclosures.stream().anyMatch(enclosure -> enclosure.contains(animal));
  }

  private Enclosure<? extends Animal> findCurrentEnclosure(Animal animal) {
    return enclosures.stream()
        .filter(enclosure -> enclosure.contains(animal))
        .findFirst()
        .orElse(null);
  }

  private void validateConsistency() {
    Map<Animal, Long> counts =
        getAllAnimalsWithoutLogging().stream()
            .collect(
                Collectors.groupingBy(animal -> animal, LinkedHashMap::new, Collectors.counting()));

    counts.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .findFirst()
        .ifPresent(
            entry ->
                LOG.severe(
                    () ->
                        "Inkonsistenz: Tier '%s' kommt in mehreren Gehegen vor."
                            .formatted(entry.getKey().name())));
  }

  private List<Animal> getAllAnimalsWithoutLogging() {
    return enclosures.stream()
        .flatMap(enclosure -> enclosure.getInhabitants().stream())
        .map(Animal.class::cast)
        .toList();
  }

  private String buildSummaryText() {
    List<Animal> animals = getAllAnimalsWithoutLogging();
    String categorySummary =
        animals.stream()
            .collect(
                Collectors.groupingBy(
                    this::categoryName, LinkedHashMap::new, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .map(entry -> "%d %s".formatted(entry.getValue(), entry.getKey()))
            .collect(Collectors.joining(", "));

    if (categorySummary.isBlank()) {
      categorySummary = "keine Tiere";
    }

    return "Zoo mit %d Gehegen und %d Tieren: %s"
        .formatted(enclosures.size(), animals.size(), categorySummary);
  }

  private String categoryName(Animal animal) {
    if (animal instanceof Mammal) {
      return "Mammals";
    }
    if (animal instanceof Bird) {
      return "Birds";
    }
    if (animal instanceof Fish) {
      return "Fish";
    }
    if (animal instanceof Reptile) {
      return "Reptiles";
    }
    return "Animals";
  }

  private static String enclosureName(Enclosure<? extends Animal> enclosure) {
    return enclosure == null ? "null" : enclosure.getName();
  }

  private void logFineState(String action) {
    LOG.fine(() -> "Zustand nach %s: %s".formatted(action, buildSummaryText()));
  }
}
