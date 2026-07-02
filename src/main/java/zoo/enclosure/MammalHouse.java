package zoo.enclosure;

import zoo.animal.Mammal;

public class MammalHouse<T extends Mammal> extends Enclosure<T> {
  public MammalHouse(String name) {
    super(name);
  }
}
