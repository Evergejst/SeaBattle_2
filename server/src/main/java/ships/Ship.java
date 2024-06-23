package ships;

import exceptions.AdjacentTilesException;
import exceptions.OverlapTilesException;
import exceptions.OversizeException;
import utils.Board;
import utils.Orientation;
import utils.Tile;
import utils.TileType;

import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный класс Ship представляет собой базовый класс для всех типов кораблей в игре "Морской бой".
 * Он содержит методы и атрибуты, общие для всех кораблей.
 */
public abstract class Ship {

    private Integer shipSize;

    /**
     * Конструктор класса Ship.
     * Устанавливает размер корабля.
     *
     * @param shipSize Размер корабля (количество ячеек, которые занимает корабль на игровом поле).
     */
    Ship(Integer shipSize) {
        this.shipSize = shipSize;
    }

    /**
     * Метод для размещения корабля на игровом поле.
     *
     * @param board        Игровое поле, на котором размещается корабль.
     * @param startingTile Начальная плитка (ячейка), с которой начинается размещение корабля.
     * @param orientation  Ориентация корабля (вертикальная или горизонтальная).
     * @throws OversizeException        Исключение, если корабль выходит за границы игрового поля.
     * @throws OverlapTilesException    Исключение, если корабль перекрывает другой корабль.
     * @throws AdjacentTilesException   Исключение, если корабль соприкасается с другим кораблем.
     */
    public void placeShip(Board board, Tile startingTile, Orientation orientation)
            throws OversizeException, OverlapTilesException, AdjacentTilesException {

        if (!shipIsInsideBoard(board, startingTile, orientation))
            throw new OversizeException("[Ship] \t" + this.getClass().getSimpleName() + " size out of bounds.");
        if (overlapsShip(board, startingTile, orientation))
            throw new OverlapTilesException("[Ship] \t" + this.getClass().getSimpleName() + " overlaps other ship.");
        if (isAdjacentToShip(board, startingTile, orientation))
            throw new AdjacentTilesException("[Ship] \t" + this.getClass().getSimpleName() + " is adjacent to another ship.");

        if (orientation == Orientation.VERTICAL) {
            for (int i = 0; i < getShipSize(); i++) {
                board.getTile(new Tile(startingTile.getX() + i, startingTile.getY())).setType(TileType.SHIP);
            }
        } else if (orientation == Orientation.HORIZONTAL) {
            for (int i = 0; i < getShipSize(); i++) {
                board.getTile(new Tile(startingTile.getX(), startingTile.getY() + i)).setType(TileType.SHIP);
            }
        }

        System.out.println("[Ship]  \t" + this.getClass().getSimpleName() + " placed successfully!");
    }

    /**
     * Метод для проверки, находится ли корабль в пределах игрового поля.
     *
     * @param board       Игровое поле.
     * @param startingTile Начальная плитка (ячейка), с которой начинается размещение корабля.
     * @param orientation Ориентация корабля (вертикальная или горизонтальная).
     * @return true, если корабль полностью находится в пределах игрового поля, иначе false.
     */
    private boolean shipIsInsideBoard(Board board, Tile startingTile, Orientation orientation) {

        boolean shipIsInsideBoard = true;

        if (orientation == Orientation.VERTICAL) {
            for (int i = 0; i < getShipSize(); i++) {
                if (!board.isInsideBoard(startingTile.getX() + i, startingTile.getY())) {
                    shipIsInsideBoard = false;
                    break;
                }
            }
        } else if (orientation == Orientation.HORIZONTAL) {
            for (int i = 0; i < getShipSize(); i++) {
                if (!board.isInsideBoard(startingTile.getX(), startingTile.getY() + i)) {
                    shipIsInsideBoard = false;
                    break;
                }
            }
        }

        return shipIsInsideBoard;
    }

    /**
     * Метод для проверки, перекрывает ли корабль другой корабль на игровом поле.
     *
     * @param board       Игровое поле.
     * @param startingTile Начальная плитка (ячейка), с которой начинается размещение корабля.
     * @param orientation Ориентация корабля (вертикальная или горизонтальная).
     * @return true, если корабль перекрывает другой корабль, иначе false.
     */
    private boolean overlapsShip(Board board, Tile startingTile, Orientation orientation) {

        boolean overlapsShip = false;

        if (orientation == Orientation.VERTICAL) {
            for (int i = 0; i < getShipSize(); i++) {
                if (board.getTile(new Tile(startingTile.getX() + i, startingTile.getY())).getType() == TileType.SHIP) {
                    overlapsShip = true;
                    break;
                }
            }
        } else if (orientation == Orientation.HORIZONTAL) {
            for (int i = 0; i < getShipSize(); i++) {
                if (board.getTile(new Tile(startingTile.getX(), startingTile.getY() + i)).getType() == TileType.SHIP) {
                    overlapsShip = true;
                    break;
                }
            }
        }

        return overlapsShip;
    }

    /**
     * Метод для проверки, соприкасается ли корабль с другим кораблем на игровом поле.
     *
     * @param board       Игровое поле.
     * @param startingTile Начальная плитка (ячейка), с которой начинается размещение корабля.
     * @param orientation Ориентация корабля (вертикальная или горизонтальная).
     * @return true, если корабль соприкасается с другим кораблем, иначе false.
     */
    private boolean isAdjacentToShip(Board board, Tile startingTile, Orientation orientation) {

        boolean isAdjacentToShip = false;
        List<Tile> adjacentTiles = new ArrayList<>();

        if (orientation == Orientation.VERTICAL) {
            for (int i = 0; i < getShipSize(); i++) {
                List<Tile> tileAdjacentTiles = board.getAdjacentTiles(new Tile(startingTile.getX() + i, startingTile.getY()));
                for (Tile tileAdjacentTile : tileAdjacentTiles) {
                    if (!adjacentTiles.contains(tileAdjacentTile))
                        adjacentTiles.add(tileAdjacentTile);
                }
            }
        } else if (orientation == Orientation.HORIZONTAL) {
            for (int i = 0; i < getShipSize(); i++) {
                List<Tile> tileAdjacentTiles = board.getAdjacentTiles(new Tile(startingTile.getX(), startingTile.getY() + i));
                for (Tile tileAdjacentTile : tileAdjacentTiles) {
                    if (!adjacentTiles.contains(tileAdjacentTile))
                        adjacentTiles.add(tileAdjacentTile);
                }
            }
        }
        for (Tile adjacentTile : adjacentTiles) {
            if (adjacentTile.getType() == TileType.SHIP) {
                isAdjacentToShip = true;
                break;
            }
        }

        return isAdjacentToShip;
    }

    /**
     * Возвращает размер корабля.
     *
     * @return Размер корабля.
     */
    private Integer getShipSize() {
        return this.shipSize;
    }
}
