package zoo.result;

public enum ZooError {
  ANIMAL_ALREADY_PRESENT("Tier ist bereits im Zielgehege vorhanden"),
  ANIMAL_NOT_FOUND("Tier wurde im Zielgehege nicht gefunden"),
  COMMAND_NOT_EXECUTED("Kommando wurde noch nicht erfolgreich ausgeführt"),
  NO_UNDO_AVAILABLE("Kein Kommando zum Rückgängigmachen vorhanden"),
  NO_REDO_AVAILABLE("Kein Kommando zum Wiederholen vorhanden");

  private final String message;

  ZooError(String message) {
    this.message = message;
  }

  public String message() {
    return message;
  }
}
