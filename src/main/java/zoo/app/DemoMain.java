package zoo.app;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import zoo.animal.Eagle;
import zoo.animal.Lion;
import zoo.animal.Penguin;
import zoo.animal.Shark;
import zoo.animal.Snake;
import zoo.animal.Trout;
import zoo.enclosure.Aquarium;
import zoo.enclosure.CatHouse;
import zoo.enclosure.Enclosure;
import zoo.enclosure.Terrarium;
import zoo.management.Zoo;

public class DemoMain {
  public static void main(String[] args) {
    Logger zooLogger = Logger.getLogger(Zoo.class.getName());
    configureLogger(zooLogger, Level.INFO);

    Zoo zoo = new Zoo();

    Aquarium<Trout> troutAquarium = new Aquarium<>("Forellen-Aquarium");
    Aquarium<Shark> sharkAquarium = new Aquarium<>("Hai-Becken");
    Terrarium<Snake> snakeTerrarium = new Terrarium<>("Schlangen-Terrarium");
    CatHouse lionHouse = new CatHouse("Loewen-Haus");
    Enclosure<Eagle> aviary = new Enclosure<>("Vogelhaus");

    zoo.addEnclosure(troutAquarium);
    zoo.addEnclosure(sharkAquarium);
    zoo.addEnclosure(snakeTerrarium);
    zoo.addEnclosure(lionHouse);
    zoo.addEnclosure(aviary);

    Trout trout = new Trout("Tina");
    Shark shark = new Shark("Sammy");
    Snake snake = new Snake("Sissi");
    Lion lion = new Lion("Leo");
    Eagle eagle = new Eagle("Erna");

    zoo.admitAnimal(troutAquarium, trout);
    zoo.admitAnimal(sharkAquarium, shark);
    zoo.admitAnimal(snakeTerrarium, snake);
    zoo.admitAnimal(lionHouse, lion);
    zoo.admitAnimal(aviary, eagle);

    System.out.println(zoo.summary());
    System.out.println("Saeugetiere: " + zoo.getAllMammals());
    System.out.println("Typzaehlung: " + zoo.countAnimalsByType());
    System.out.println("Ueberfuellte Gehege bei max. 0 Tieren: " + zoo.getOvercrowdedEnclosures(0));

    // Umschalten auf FINE: Jetzt erscheinen auch Zustandszusammenfassungen.
    configureLogger(zooLogger, Level.FINE);
    zoo.releaseAnimal(new Penguin("Nicht im Zoo"));
    zoo.moveAnimal(trout, troutAquarium);

    System.out.println(zoo.summary());
  }

  private static void configureLogger(Logger logger, Level level) {
    logger.setUseParentHandlers(false);
    logger.setLevel(level);

    for (Handler handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }

    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(level);
    handler.setFormatter(new SimpleFormatter());
    logger.addHandler(handler);
  }
}
