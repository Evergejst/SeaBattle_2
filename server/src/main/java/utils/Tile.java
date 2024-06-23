package utils;

/**
 * Класс Tile представляет плитку игрового поля с координатами и типом.
 */
public class Tile {

    private Integer x, y;
    private TileType type;

    /**
     * Конструктор для создания плитки с заданными координатами.
     * @param x Координата x плитки.
     * @param y Координата y плитки.
     */
    public Tile(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Конструктор для создания плитки с заданными координатами и типом.
     * @param x Координата x плитки.
     * @param y Координата y плитки.
     * @param type Тип плитки (SEA, SHIP, HIT, MISS).
     */
    public Tile(Integer x, Integer y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Возвращает символьное представление плитки, учитывая возможность скрыть тип SHIP.
     * @param hidden true, если тип SHIP должен быть скрыт, false - иначе.
     * @return Символьное представление плитки.
     */
    String draw(boolean hidden) {
        String tile;
        switch (this.type) {
            case HIT:
                tile = "\t\u001B[32mX\u001B[0m"; // Зеленый X для попадания
                break;
            case SEA:
                tile = "\t\u001B[34m~\u001B[0m"; // Синяя волна для моря
                break;
            case MISS:
                tile = "\t\u001B[31mo\u001B[0m"; // Красное o для промаха
                break;
            case SHIP:
                if (hidden)
                    tile = "\t\u001B[34m~\u001B[0m"; // Скрытый SHIP как море
                else
                    tile = "\t\u001B[32ms\u001B[0m"; // Зеленая s для корабля
                break;
            default:
                tile = "\t\u001B[34m~\u001B[0m"; // По умолчанию - море
                break;
        }
        return tile;
    }

    /**
     * Возвращает координату y плитки.
     * @return Координата y плитки.
     */
    public Integer getY() {
        return y;
    }

    /**
     * Возвращает координату x плитки.
     * @return Координата x плитки.
     */
    public Integer getX() {
        return x;
    }

    /**
     * Возвращает тип плитки.
     * @return Тип плитки (SEA, SHIP, HIT, MISS).
     */
    public TileType getType() {
        return type;
    }

    /**
     * Устанавливает тип плитки.
     * @param type Тип плитки (SEA, SHIP, HIT, MISS).
     */
    public void setType(TileType type) {
        this.type = type;
    }
}
