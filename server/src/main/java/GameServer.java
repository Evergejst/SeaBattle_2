import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import utils.Board;
import utils.Tile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private WorkWithTime workWithTime = new WorkWithTime();
    private static final String GAMES_DIRECTORY = ".\\";
    private boolean isAdmin = false;
    private final LoggingGame loggingGame = new LoggingGame();

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
            String startTime = workWithTime.getCurrentTime();
            logWriter.println("Game started at " + workWithTime.getCurrentTime());
            logWriter.println("Player 1: " + playerName1);
            if (singlePlayerMode) {
                logWriter.println("Player 2: Computer");
            } else {
                logWriter.println("Player 2: " + playerName2);
            }
            logWriter.println();
            workWithTime.setStartTime(System.currentTimeMillis());
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
                FileDeleter.deleteGame(GAMES_DIRECTORY, fileName);
                break;
            case "archive":
                FileArchiver.archiveGame(GAMES_DIRECTORY, fileName);
                break;
            case "view":
                FormerListMoves.formListMoves(conn, fileName);
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
        loggingGame.logMove(saveWriter, currentPlayer.getName(), move[0], move[1], moveResult);
        saveWriter.println(currentPlayer.getBoard().drawBoardForLog());
        saveWriter.flush();

        conn.send(moveResult);
        conn.send(opponentPlayer.getBoard().drawBoard(true));
        if (opponentPlayer.getBoard().allShipsSunk()) {
            loggingGame.logMove(saveWriter, opponentPlayer.getName(), move[0], move[1], moveResult);
            saveWriter.println(opponentPlayer.getBoard().drawBoardForLog());
            saveWriter.flush();
            saveWriters.println(nameSaveFile);
            saveWriters.flush();
            loggingGame.logGameEnd(logWriter, currentPlayer, opponentPlayer, workWithTime.getStartTime());
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
            loggingGame.logMove(saveWriter, currentPlayer.getName(), move[0], move[1], moveResult);
            saveWriter.println(currentPlayer.getBoard().drawBoardForLog());
            saveWriter.flush();
        } else {
            moveResult = opponentPlayer.fire(currentPlayer.getBoard(), new Tile(move[0], move[1]));
            loggingGame.logMove(saveWriter, opponentPlayer.getName(), move[0], move[1], moveResult);
            saveWriter.println(opponentPlayer.getBoard().drawBoardForLog());
            saveWriter.flush();
        }
        conn.send(moveResult);
        conn.send(currentPlayer.getBoard().drawBoard(false));
        conn.send(opponentPlayer.getBoard().drawBoard(false));
        if (opponentPlayer.getBoard().allShipsSunk()) {
            saveWriters.println(nameSaveFile);
            saveWriters.flush();
            loggingGame.logGameEnd(logWriter, currentPlayer, opponentPlayer, workWithTime.getStartTime());
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
            loggingGame.logGameEnd(logWriter, opponentPlayer, currentPlayer, workWithTime.getStartTime());
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

    public static void main(String[] args) {
        int port = 8887;
        GameServer server = new GameServer(port);
        server.start();
        System.out.println("Game server started on port: " + port);
    }
}