package exceptions;

/**
 * Исключение OversizeException используется для обработки ситуаций, когда размер корабля превышает
 * допустимые границы игрового поля при его размещении.
 */
public class OversizeException extends Exception {

    /**
     * Конструктор класса OversizeException.
     * @param message сообщение об ошибке
     */
    public OversizeException(String message) {
        super(message);
    }
}
