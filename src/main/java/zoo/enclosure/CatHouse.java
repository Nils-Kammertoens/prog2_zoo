package zoo.enclosure;

import zoo.animal.Lion;

/**
 * CatHouse is intentionally restricted to one concrete Cat implementation. Here, only Lion objects
 * are accepted.
 */
public class CatHouse extends Enclosure<Lion> {
  public CatHouse(String name) {
    super(name);
  }
}
