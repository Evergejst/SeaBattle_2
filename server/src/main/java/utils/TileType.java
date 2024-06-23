package utils;

/**
 * Перечисление TileType представляет типы плиток на игровом поле.
 */
public enum TileType {
    SEA,  // Море (пустая плитка)
    SHIP, // Корабль
    HIT,  // Попадание по кораблю
    MISS  // Промах
}
