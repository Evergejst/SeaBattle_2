package ships;

/**
 * Класс AircraftCarrier представляет собой тип корабля Aircraft Carrier (авианосец) в игре "Морской бой".
 * Унаследован от абстрактного класса Ship.
 * Авианосец имеет размер 6 ячеек на игровом поле.
 */
public class AircraftCarrier extends Ship {

    /**
     * Конструктор класса AircraftCarrier.
     * Вызывает конструктор суперкласса Ship с указанием размера 6.
     */
    public AircraftCarrier() {
        super(6); // Размер авианосца составляет 6 ячеек
    }
}

