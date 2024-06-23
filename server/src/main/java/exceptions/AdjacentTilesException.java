package exceptions;

/**
 * Исключение AdjacentTilesException используется для обработки ситуаций, когда корабль пытаются разместить
 * смежно с другим кораблем на игровом поле.
 */
public class AdjacentTilesException extends Exception {

    /**
     * Конструктор класса AdjacentTilesException.
     * @param message сообщение об ошибке
     */
    public AdjacentTilesException(String message) {
        super(message);
    }
}
