package utils;

import exceptions.AdjacentTilesException;
import exceptions.OverlapTilesException;
import exceptions.OversizeException;
import ships.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Класс Board представляет игровое поле для морского боя.
 */
public class Board {

    private Tile[][] board;
    private Integer boardSize;

    /**
     * Конструктор для создания игрового поля заданного размера.
     *
     * @param boardSize Размер игрового поля (должен быть одинаковым по обеим измерениям).
     */
    public Board(Integer boardSize) {
        this.boardSize = boardSize;
        this.board = new Tile[boardSize][boardSize];
    }

    /**
     * Инициализация игрового поля морского боя, заполняя его морскими клетками.
     */
    public void initBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                this.board[i][j] = new Tile(i, j, TileType.SEA);
            }
        }
    }

    /**
     * Возвращает строковое представление игрового поля для вывода на консоль.
     *
     * @param hidden Флаг скрытости (true - скрытый режим, false - открытый режим).
     * @return Строковое представление игрового поля.
     */
    public String drawBoard(boolean hidden) {
        StringBuilder sb = new StringBuilder();
        System.out.println();
        char startChar = 'A';
        char endChar = (char) (startChar + boardSize - 1);

        StringBuilder alphabetLine = new StringBuilder();

        for (char ch = startChar; ch <= endChar; ch++) {
            alphabetLine.append("\t").append(ch);
            sb.append("\t").append(ch);
        }

        sb.append(System.lineSeparator());
        System.out.print(alphabetLine.toString());

        for (int i = 0; i < boardSize; i++) {
            System.out.print(i);
            for (int j = 0; j < boardSize; j++) {
                sb.append(this.board[i][j].draw(hidden)).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Возвращает строковое представление игрового поля для логирования.
     *
     * @return Строковое представление игрового поля для логирования.
     */
    public String drawBoardForLog() {
        StringBuilder sb = new StringBuilder();
        System.out.println();
        char startChar = 'A';
        char endChar = (char) (startChar + boardSize - 1);

        StringBuilder alphabetLine = new StringBuilder();

        for (char ch = startChar; ch <= endChar; ch++) {
            alphabetLine.append("\t").append(ch);
            sb.append("\t").append(ch);
        }

        sb.append(System.lineSeparator());

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                switch (this.board[i][j].draw(false)) {
                    case "\t\u001B[32mX\u001B[0m":
                        sb.append("\tX");
                        break;
                    case "\t\u001B[34m~\u001B[0m":
                        sb.append("\t~");
                        break;
                    case "\t\u001B[31mo\u001B[0m":
                        sb.append("\to");
                        break;
                    case "\t\u001B[32ms\u001B[0m":
                        sb.append("\tS");
                        break;
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Возвращает список соседних клеток для заданной клетки на игровом поле.
     *
     * @param tile Клетка, для которой необходимо найти соседей.
     * @return Список соседних клеток.
     */
    public List<Tile> getAdjacentTiles(Tile tile) {
        ArrayList<Tile> adjacentTiles = new ArrayList<>();

        if (isInsideBoard(tile.getX(), tile.getY())) {
            if (isInsideBoard(tile.getX() + 1, tile.getY()))
                adjacentTiles.add(board[tile.getX() + 1][tile.getY()]);
            if (isInsideBoard(tile.getX() - 1, tile.getY()))
                adjacentTiles.add(board[tile.getX() - 1][tile.getY()]);
            if (isInsideBoard(tile.getX(), tile.getY() + 1))
                adjacentTiles.add(board[tile.getX()][tile.getY() + 1]);
            if (isInsideBoard(tile.getX(), tile.getY() - 1))
                adjacentTiles.add(board[tile.getX()][tile.getY() - 1]);
            if (isInsideBoard(tile.getX() - 1, tile.getY() + 1))
                adjacentTiles.add(board[tile.getX() - 1][tile.getY() + 1]);
            if (isInsideBoard(tile.getX() + 1, tile.getY() - 1))
                adjacentTiles.add(board[tile.getX() + 1][tile.getY() - 1]);
            if (isInsideBoard(tile.getX() + 1, tile.getY() + 1))
                adjacentTiles.add(board[tile.getX() + 1][tile.getY() + 1]);
            if (isInsideBoard(tile.getX() - 1, tile.getY() - 1))
                adjacentTiles.add(board[tile.getX() - 1][tile.getY() - 1]);
        }
        return adjacentTiles;
    }

    /**
     * Размещает все корабли случайным образом на игровом поле.
     */
    public void placeAllShips() {
        placeShipRandom(new AircraftCarrier());
        for (int i = 0; i < 2; i++) {
            placeShipRandom(new Carrier());
        }
        for (int i = 0; i < 3; i++) {
            placeShipRandom(new BattleShip());
        }
        for (int i = 0; i < 4; i++) {
            placeShipRandom(new Cruiser());
        }
        for (int i = 0; i < 5; i++) {
            placeShipRandom(new Destroyer());
        }
        for (int i = 0; i < 6; i++) {
            placeShipRandom(new BattleBoat());
        }
        System.out.println("[Board] \tAll ships placed successfully!");
    }

    /**
     * Попытка размещения корабля на игровом поле в случайном месте и с случайной ориентацией.
     *
     * @param ship Корабль для размещения.
     */
    private void placeShipRandom(Ship ship) {
        System.out.println("[Board] \tTrying to place ships...");
        boolean flag = true;

        while (flag) {
            try {
                ship.placeShip(this, new Tile(randomGenerator(0, boardSize), randomGenerator(0, boardSize)), getRandomOrientation());
                flag = false;
            } catch (OversizeException | OverlapTilesException | AdjacentTilesException e) {
                System.out.println("[Board] \tTrying to place ships...");
            }
        }

    }

    /**
     * Возвращает случайную ориентацию: вертикальную или горизонтальную.
     *
     * @return Случайная ориентация корабля.
     */
    private Orientation getRandomOrientation() {
        int randomOrientation = randomGenerator(0, 1);
        return (randomOrientation == 0) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
    }

    /**
     * Проверяет, находится ли заданная клетка внутри игрового поля.
     *
     * @param i Координата x клетки.
     * @param j Координата y клетки.
     * @return true, если клетка находится внутри игрового поля, иначе false.
     */
    public boolean isInsideBoard(int i, int j) {
        return i >= 0 && i < boardSize && j >= 0 && j < boardSize;
    }

    /**
     * Генерирует случайное число в заданном диапазоне.
     *
     * @param minimum Минимальное значение диапазона.
     * @param maximum Максимальное значение диапазона.
     * @return Случайное число в заданном диапазоне.
     */
    private int randomGenerator(int minimum, int maximum) {
        Random rn = new Random();
        int range = maximum - minimum + 1;
        return rn.nextInt(range) + minimum;
    }

    /**
     * Проверяет, все ли корабли на игровом поле потоплены.
     *
     * @return true, если все корабли потоплены, иначе false.
     */
    public boolean allShipsSunk() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j].getType() == TileType.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Возвращает объект клетки по заданным координатам.
     *
     * @param tile Объект клетки с заданными координатами.
     * @return Объект клетки с заданными координатами.
     */
    public Tile getTile(Tile tile) {
        return board[tile.getX()][tile.getY()];
    }
}
