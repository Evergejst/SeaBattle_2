package exceptions;

/**
 * Исключение OverlapTilesException используется для обработки ситуаций, когда корабль перекрывает
 * другой корабль при размещении на игровом поле.
 */
public class OverlapTilesException extends Exception {

    /**
     * Конструктор класса OverlapTilesException.
     * @param message сообщение об ошибке
     */
    public OverlapTilesException(String message) {
        super(message);
    }
}
