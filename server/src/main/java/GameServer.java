import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import utils.Board;
import utils.Tile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GameServer extends WebSocketServer {

    private List<WebSocket> players = new ArrayList<>();
    private Player player1;
    private Player player2;
    private boolean player1Turn = true;
    private static Integer boardSize = 16;
    private boolean singlePlayerMode = false;
    private String playerName1;
    private String playerName2;
    private String nameSaveFile;
    private PrintWriter logWriter;
    private PrintWriter saveWriter;
    private PrintWriter saveWriters;
    private long startTime;
    private static final String GAMES_DIRECTORY = ".\\";
    private static final String GAME_FILE_EXTENSION = ".txt";
    private boolean isAdmin = false;

    public GameServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("onOpen");
        conn.send("Welcome to Sea Battle!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("onClose");
        players.remove(conn);
        if (players.size() == 1) {
            players.get(0).send("Opponent disconnected. You win!");
            players.get(0).close();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("onMessage");
        if (!isAdmin) {
            if (message.startsWith("move")) {
                String[] parts = message.substring(5).split(",");
                Integer[] move = new Integer[]{Integer.parseInt(parts[0]) - 1, parts[1].charAt(0) - 'A'};
                handleMoveResponse(conn, move);
            } else if (message.startsWith("mode")) {
                handleGameModeSelection(conn, message);
            } else if (message.startsWith("The player")) {
                if (players.size() < 1) {
                    playerName1 = message.substring(29);
                    System.out.println(playerName1);
                } else {
                    playerName2 = message.substring(29);
                    System.out.println(playerName2);
                }
            }
            else if (message.startsWith("admin")) {
                isAdmin = true;
                conn.send("You have entered the admin menu, here you can delete, archive or view all the players' moves\n" +
                        "An example of the command (delete, archive, view) file_name_of_the_game.txt");
            }
        }
        else {
            System.out.println(message);
            handleAdminCommands(conn, message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("onError");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("onStart");
        System.out.println("Server started on port: " + getPort());
    }

    private void startGame() {
        System.out.println("startGame");

        try {
            logWriter = new PrintWriter(new FileWriter("game_log.txt", true));
            saveWriters = new PrintWriter(new FileWriter("all_name_saves.txt", true));
            List<String> lines = Files.readAllLines(Paths.get("all_name_saves.txt"));
            int numSave = lines.size();
            if (numSave == 17) {
                try (PrintWriter allWriters = new PrintWriter("all_name_saves.txt")) {
                    for (int i = 1; i <= 17; i++) {
                        try (PrintWriter allSaves = new PrintWriter("save_" + i + ".txt")) {

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    numSave = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            nameSaveFile = "save_" + (numSave + 1) + ".txt";
            saveWriter = new PrintWriter(new FileWriter(nameSaveFile, true));
            logWriter.println("Game started at " + getCurrentTime());
            logWriter.println("Player 1: " + playerName1);
            if (singlePlayerMode) {
                logWriter.println("Player 2: Computer");
            } else {
                logWriter.println("Player 2: " + playerName2);
            }
            logWriter.println();
            startTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Board player1Board = new Board(boardSize);
        player1Board.initBoard();
        player1 = new Player(playerName1, player1Board);
        player1.placeAllShips();
        if (singlePlayerMode) {
            Board computerBoard = new Board(boardSize);
            computerBoard.initBoard();
            player2 = new Player("Computer", computerBoard);
            player2.placeAllShips();
            players.get(0).send("Game start! You are playing against the computer. Your move.");
            players.get(0).send("[Your Board]\n" + player1Board.drawBoard(false));
        } else {
            Board player2Board = new Board(boardSize);
            player2Board.initBoard();
            player2 = new Player(playerName2, player2Board);
            player2.placeAllShips();

            players.get(0).send("Game start! Your move.");
            players.get(0).send(player1Board.drawBoard(false));
            players.get(1).send(player2Board.drawBoard(false));

        }
        nextTurn();
    }

    private static Integer[] getRandInput() {
        Random rn = new Random();
        int range = boardSize;
        Integer[] randInputs = new Integer[2];
        randInputs[0] = rn.nextInt(range);
        randInputs[1] = rn.nextInt(range);
        return randInputs;
    }

    private void handleGameModeSelection(WebSocket conn, String message) {
        System.out.println("handleGameModeSelection");
        if (message.contains("1")) {
            singlePlayerMode = true;
            players.add(conn);
            conn.send("You chose single player mode.");
            startGame();
        } else if (message.contains("2")) {
            singlePlayerMode = false;
            players.add(conn);
            conn.send("You chose multiplayer mode.");
            if (players.size() == 2) {
                startGame();
            }
        }
    }

    private void nextTurn() {
        System.out.println("nextTurn");
        if (!singlePlayerMode) {
            WebSocket currentPlayer = player1Turn ? players.get(0) : players.get(1);
            // WebSocket opponent = player1Turn ? players.get(1) : players.get(0);

            currentPlayer.send("Your turn. Enter coordinates to fire (e.g., 3,B):");
        } else {
            if (player1Turn) {
                players.get(0).send("Your turn. Enter coordinates to fire (e.g., 3,B):");
            } else {
                Integer[] computerShots = getRandInput();
                handleMoveResponse(players.get(0), computerShots);
            }
        }
    }

    private void handleAdminCommands(WebSocket conn, String message) {
        String[] words = message.split(" ");
        String command = words[0];
        String fileName = words[1];
        switch (command) {
            case "delete":
                deleteGame(fileName);
                break;
            case "archive":
                archiveGame(fileName);
                break;
            case "view":
                formListMoves(conn, fileName);
                break;
        }
        conn.send("Keep typing commands\n" +
                "An example of the command (delete, archive, view) file_name_of_the_game.txt");
    }

    private void handleMoveResponse(WebSocket conn, Integer[] move) {
        System.out.println("handleMoveResponse");
        if (move[0] < boardSize && move[1] < boardSize) {
            System.out.println("Received move: " + move[0] + " " + move[1]);
            if (singlePlayerMode) {
                processMoveForSingleMod(conn, move);
            } else {
                processMoveForNotSingleMod(conn, move);
            }
        } else {
            conn.send("[Game] \tWrong coordinates please try again.");
            nextTurn();
        }
    }

    private void processMoveForNotSingleMod(WebSocket conn, Integer[] move) {
        System.out.println("[Game] Player made a move: " + move[0] + " " + move[1]);

        WebSocket opponent = getOpponent(conn);
        Player currentPlayer = conn == players.get(0) ? player1 : player2;
        Player opponentPlayer = conn == players.get(0) ? player2 : player1;

        String moveResult = currentPlayer.fire(opponentPlayer.getBoard(), new Tile(move[0], move[1]));
        logMove(saveWriter, currentPlayer.getName(), move[0], move[1], moveResult);
        saveWriter.println(currentPlayer.getBoard().drawBoardForLog());
        saveWriter.flush();

        conn.send(moveResult);
        conn.send(opponentPlayer.getBoard().drawBoard(true));
        if (opponentPlayer.getBoard().allShipsSunk()) {
            logMove(saveWriter, opponentPlayer.getName(), move[0], move[1], moveResult);
            saveWriter.println(opponentPlayer.getBoard().drawBoardForLog());
            saveWriter.flush();
            saveWriters.println(nameSaveFile);
            saveWriters.flush();
            logGameEnd(currentPlayer, opponentPlayer);
            conn.send("You win!");
            opponent.send("You lose!");
            try {
                stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            player1Turn = !player1Turn;
            nextTurn();
        }
    }

    private void processMoveForSingleMod(WebSocket conn, Integer[] move) {
        System.out.println("[Game] Player made a move: " + move[0] + " " + move[1]);
        Player currentPlayer = player1;
        Player opponentPlayer = player2;

        String moveResult;
        if (player1Turn) {
            moveResult = currentPlayer.fire(opponentPlayer.getBoard(), new Tile(move[0], move[1]));
            logMove(saveWriter, currentPlayer.getName(), move[0], move[1], moveResult);
            saveWriter.println(currentPlayer.getBoard().drawBoardForLog());
            saveWriter.flush();
        } else {
            moveResult = opponentPlayer.fire(currentPlayer.getBoard(), new Tile(move[0], move[1]));
            logMove(saveWriter, opponentPlayer.getName(), move[0], move[1], moveResult);
            saveWriter.println(opponentPlayer.getBoard().drawBoardForLog());
            saveWriter.flush();
        }
        conn.send(moveResult);
        conn.send(currentPlayer.getBoard().drawBoard(false));
        conn.send(opponentPlayer.getBoard().drawBoard(false));
        if (opponentPlayer.getBoard().allShipsSunk()) {
            saveWriters.println(nameSaveFile);
            saveWriters.flush();
            logGameEnd(currentPlayer, opponentPlayer);
            conn.send("You win!");
            try {
                stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if (currentPlayer.getBoard().allShipsSunk()) {
            saveWriter.println(currentPlayer.getBoard().drawBoardForLog());
            saveWriters.flush();
            saveWriters.println(nameSaveFile);
            saveWriters.flush();
            logGameEnd(opponentPlayer, currentPlayer);
            conn.send("You loser!");
        }
        else {
            player1Turn = !player1Turn;
            nextTurn();
        }
    }

    private WebSocket getOpponent(WebSocket conn) {
        return conn == players.get(0) ? players.get(1) : players.get(0);
    }

    private void logMove(PrintWriter someWriter, String playerName, int x, int y, String result) {
        if (someWriter != null) {
            someWriter.printf("Move by %s at %s: (%d, %c) - %s%n", playerName, getCurrentTime(), x + 1, (char) ('A' + y), result);
            someWriter.flush();
        }
    }

    private void logGameEnd(Player winner, Player loser) {
        if (logWriter != null) {
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;

            logWriter.println();
            logWriter.println("Game ended at " + getCurrentTime());
            logWriter.printf("Duration: %d seconds%n", duration);
            logWriter.printf("Winner: %s%n", winner.getName());
            logWriter.printf("Loser: %s%n", loser.getName());
            logWriter.println("Final Boards:");
            logWriter.printf("Player 1 (%s) Board:%n", player1.getName());
            logWriter.println(winner.getBoard().drawBoardForLog());
            logWriter.printf("Player 2 (%s) Board:%n", player2.getName());
            logWriter.println(loser.getBoard().drawBoardForLog());
            logWriter.println();

            logWriter.close();
        }
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    // Метод для удаления игры
    private static boolean deleteGame(String gameFileName) {
        File file = new File(GAMES_DIRECTORY, gameFileName);
        boolean isExist = file.exists() && file.delete();
        return isExist;
    }

    // Метод для архивирования игры
    private static boolean archiveGame(String gameFileName) {
        File file = new File(GAMES_DIRECTORY, gameFileName);
        if (!file.exists()) {
            return false;
        }

        try {
            String ARCHIVE_DIRECTORY = "FLKsajfklsdj";
            File archiveDir = new File(ARCHIVE_DIRECTORY);
            if (!archiveDir.exists()) {
                archiveDir.mkdir();
            }

            String zipFileName = gameFileName.replace(GAME_FILE_EXTENSION, ".zip");
            FileOutputStream fos = new FileOutputStream(new File(ARCHIVE_DIRECTORY, zipFileName));
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.putNextEntry(new ZipEntry(file.getName()));

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            zos.close();
            fis.close();
            fos.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для воспроизведения игры
    private static void formListMoves(WebSocket conn, String gameFileName) {
        // Считать журнал игры из файла
        List<String> gameLogs = readGameLog(gameFileName);

        // Проверка на пустоту журнала
        if (gameLogs.isEmpty()) {
            System.out.println("No game log data found.");
            return; // Завершаем метод, если журнал пуст
        }

        // Разделение журнала на ходы
        List<List<String>> moves = parseMoves(gameLogs);

        // Преобразуем объект moves в строку
        String movesString = serializeMoves(moves);

        // Отправляем строку через веб-сокет
        conn.send(movesString);
    }

    private static String serializeMoves(List<List<String>> moves) {
        StringBuilder sb = new StringBuilder();
        for (List<String> move : moves) {
            for (String line : move) {
                sb.append(line).append("\n"); // Разделяем строки символом новой строки
            }
            sb.append("\n"); // Разделяем ходы пустой строкой
        }
        return sb.toString();
    }

    private static List<String> readGameLog(String filePath) {
        List<String> gameLogs = new ArrayList<>();
        try {
            gameLogs = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error reading game log: " + e.getMessage());
        }
        return gameLogs;
    }

    private static List<List<String>> parseMoves(List<String> gameLogs) {
        List<List<String>> moves = new ArrayList<>();
        List<String> currentMove = new ArrayList<>();

        for (String line : gameLogs) {
            if (line.startsWith("Move by")) {
                if (!currentMove.isEmpty()) {
                    moves.add(new ArrayList<>(currentMove));
                    currentMove.clear();
                }
            }
            currentMove.add(line);
        }

        if (!currentMove.isEmpty()) {
            moves.add(currentMove);
        }

        return moves;
    }

    public static void main(String[] args) {
        int port = 8887;
        GameServer server = new GameServer(port);
        server.start();
        System.out.println("Game server started on port: " + port);
    }
}