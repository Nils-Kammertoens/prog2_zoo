package zoo.enclosure;

import zoo.animal.Fish;

public class Aquarium<T extends Fish> extends Enclosure<T> {
  public Aquarium(String name) {
    super(name);
  }
}
