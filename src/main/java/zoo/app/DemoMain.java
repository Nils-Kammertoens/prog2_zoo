package zoo.app;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import zoo.animal.Gorilla;
import zoo.animal.Lion;
import zoo.animal.Mammal;
import zoo.animal.Penguin;
import zoo.animal.Shark;
import zoo.animal.Snake;
import zoo.animal.Trout;
import zoo.command.AddAnimalCommand;
import zoo.command.CommandManager;
import zoo.command.RemoveAnimalCommand;
import zoo.enclosure.Aquarium;
import zoo.enclosure.CatHouse;
import zoo.enclosure.Enclosure;
import zoo.enclosure.MammalHouse;
import zoo.enclosure.Terrarium;
import zoo.management.Zoo;

public class DemoMain {
  public static void main(String[] args) {
    configureLogger(Logger.getLogger(Zoo.class.getName()), Level.INFO);
    configureLogger(Logger.getLogger(CommandManager.class.getName()), Level.FINE);

    Zoo zoo = new Zoo();

    Aquarium<Trout> troutAquarium = new Aquarium<>("Forellen-Aquarium");
    Aquarium<Shark> sharkAquarium = new Aquarium<>("Hai-Becken");
    Terrarium<Snake> snakeTerrarium = new Terrarium<>("Schlangen-Terrarium");
    Enclosure<Mammal> mammalHouse = new MammalHouse<>("Saeugetier-Haus");
    CatHouse lionHouse = new CatHouse("Loewen-Haus");

    zoo.addEnclosure(troutAquarium);
    zoo.addEnclosure(sharkAquarium);
    zoo.addEnclosure(snakeTerrarium);
    zoo.addEnclosure(mammalHouse);
    zoo.addEnclosure(lionHouse);

    zoo.admitAnimal(troutAquarium, new Trout("Tina"));
    zoo.admitAnimal(sharkAquarium, new Shark("Sammy"));
    zoo.admitAnimal(snakeTerrarium, new Snake("Sissi"));

    CommandManager<Enclosure<Mammal>> mammalManager = new CommandManager<>();
    CommandManager<Enclosure<Lion>> lionManager = new CommandManager<>();

    AddAnimalCommand<Lion> addLeo = new AddAnimalCommand<>(new Lion("Leo"));
    AddAnimalCommand<Gorilla> addGina = new AddAnimalCommand<>(new Gorilla("Gina"));
    RemoveAnimalCommand<Lion> removeLeo = new RemoveAnimalCommand<>(new Lion("Leo"));

    mammalManager.executeCommand(addLeo, mammalHouse);
    mammalManager.executeCommand(addGina, mammalHouse);
    mammalManager.executeCommand(removeLeo, mammalHouse);
    mammalManager.undo(mammalHouse);
    mammalManager.redo(mammalHouse);

    AddAnimalCommand<Lion> addFelix = new AddAnimalCommand<>(new Lion("Felix"));
    lionManager.executeCommand(addFelix, lionHouse);

    // Das wird vom Compiler abgelehnt:
    // AddAnimalCommand<Shark> addNemo = new AddAnimalCommand<>(new Shark("Nemo"));
    // mammalManager.executeCommand(addNemo, mammalHouse);

    System.out.println(zoo.summary());
    System.out.println("Suche Felix: " + zoo.findAnimalByName("Felix"));
    System.out.println("Suche unbekanntes Tier: " + zoo.findAnimalByName("NichtDa"));
    zoo.releaseAnimal(new Penguin("Nicht im Zoo"));
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
