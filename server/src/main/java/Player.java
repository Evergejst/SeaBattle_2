import utils.*;

/**
 * Класс Player представляет игрока в игре "Морской бой".
 */
class Player {

    private String name;            // Имя игрока
    private Board board;            // Игровое поле игрока
    private Integer shots;          // Количество выстрелов
    private Integer successfulShots; // Количество успешных выстрелов
    private Integer repeatedShots;  // Количество повторных выстрелов

    /**
     * Конструктор для инициализации игрока.
     * @param name Имя игрока
     * @param board Игровое поле игрока
     */
    Player(String name, Board board) {
        this.name = name;
        this.shots = 0;
        this.successfulShots = 0;
        this.repeatedShots = 0;
        this.board = board;
    }

    /**
     * Размещает все корабли на игровом поле.
     */
    void placeAllShips() {
        getBoard().placeAllShips();
    }

    /**
     * Выполняет выстрел по заданной позиции на доске.
     * @param board Доска противника, по которой производится выстрел
     * @param tile Позиция выстрела
     * @return Строка, описывающая результат выстрела
     */
    String fire(Board board, Tile tile) {
        String fireResult = "";
        switch (board.getTile(tile).getType()) {
            case SEA:
                board.getTile(tile).setType(TileType.MISS);
                shots++;
                fireResult = String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString());
                System.out.println(String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString()));
                break;
            case SHIP:
                board.getTile(tile).setType(TileType.HIT);
                shots++;
                successfulShots++;
                fireResult = String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString());
                System.out.println(String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString()));
                break;
            case HIT:
                board.getTile(tile).setType(TileType.HIT);
                shots++;
                repeatedShots++;
                fireResult = String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) Уже %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString());
                System.out.println(String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) Уже %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString()));
                break;
            case MISS:
                board.getTile(tile).setType(TileType.MISS);
                shots++;
                repeatedShots++;
                fireResult = String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString());
                System.out.println(String.format("[Player] \t%s сделал выстрел в клетку (%d, %d) Уже %s.",
                        getName(), tile.getX(), tile.getY(), board.getTile(tile).getType().toString()));
                break;
        }
        return fireResult;
    }

    /**
     * Возвращает имя игрока.
     * @return Имя игрока
     */
    String getName() {
        return name;
    }

    /**
     * Возвращает игровое поле игрока.
     * @return Игровое поле игрока
     */
    public Board getBoard() {
        return this.board;
    }
}
