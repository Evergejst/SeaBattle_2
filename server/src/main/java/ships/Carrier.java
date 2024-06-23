package ships;

/**
 * Класс Carrier представляет собой тип корабля Carrier (авианосец) в игре "Морской бой".
 * Унаследован от абстрактного класса Ship.
 * Авианосец имеет размер 5 ячеек на игровом поле.
 */
public class Carrier extends Ship {

    /**
     * Конструктор класса Carrier.
     * Вызывает конструктор суперкласса Ship с указанием размера 5.
     */
    public Carrier() {
        super(5); // Размер авианосца составляет 5 ячеек
    }
}
