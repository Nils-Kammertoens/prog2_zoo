package zoo.animal;

/** Base type for all animals in this simplified zoo model. */
public sealed interface Animal permits Mammal, Bird, Fish, Reptile {
  String name();
}
