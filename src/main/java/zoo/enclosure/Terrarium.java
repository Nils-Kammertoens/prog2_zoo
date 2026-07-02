package zoo.enclosure;

import zoo.animal.Reptile;

public class Terrarium<T extends Reptile> extends Enclosure<T> {
  public Terrarium(String name) {
    super(name);
  }
}
