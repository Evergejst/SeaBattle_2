import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class GameClient extends WebSocketClient {

    private Scanner scanner = new Scanner(System.in);
    private static final int MAX_MESSAGE_SIZE = 1024; // Максимальный размер сообщения

    public GameClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server.");
        System.out.println("Enter your name");
        String name = scanner.nextLine();
        send("The player has chosen a name " + name);
        if (name.equals("admin")) {
            this.send("admin");
        }
        else {
            selectGameMode();
        }
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("Move by")) {
            replayGame(message);
        }
        else {
            System.out.println(message);
            if (message.contains("Your turn")) {
                makeMove();
            } else if (message.contains("You win") || message.contains("You lose") || message.contains("Game over")) {
                this.close();
            } else if (message.contains("An example of the command (delete, archive, view) file_name_of_the_game.txt")) {
                String command = scanner.nextLine();
                this.send(command);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void makeMove() {

        System.out.print("Enter your move (format: x y): ");
        String move = scanner.nextLine();
        try {
            String[] parts = move.split(",");
            Integer[] intMove = new Integer[]{Integer.parseInt(parts[0]) - 1, parts[1].charAt(0) - 'A'};
        }
        catch (Exception e) {
            makeMove();
        }

        // Проверяем длину сообщения перед отправкой
        if (move.length() > MAX_MESSAGE_SIZE) {
            System.out.println("Error: Move command is too large.");
            return;
        }

        try {
            this.send("move " + move);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending move: " + e.getMessage());
        }
    }

    private void selectGameMode() {
        System.out.println("Do you want to play single (1) or multiplayer (2)?");
        String mode = scanner.nextLine();
        if (mode.equals("1") || mode.equals("2")) {
            this.send("mode " + mode);
        }
        else {
            System.out.println("Wrong command, enter it again");
            selectGameMode();
        }
    }

    private static void replayGame(String movesString) {
        List<List<String>> moves = deserializeMoves(movesString);

        // Проигрываем игру
        Scanner scanner = new Scanner(System.in);
        for (List<String> move : moves) {
            System.out.println("Press Enter to see the next move...");
            scanner.nextLine();
            for (String line : move) {
                System.out.println(line);
            }
        }
    }

    private static List<List<String>> deserializeMoves(String movesString) {
        List<List<String>> moves = new ArrayList<>();
        List<String> currentMove = new ArrayList<>();

        Scanner scanner = new Scanner(movesString);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                if (!currentMove.isEmpty()) {
                    moves.add(new ArrayList<>(currentMove));
                    currentMove.clear();
                }
            } else {
                currentMove.add(line);
            }
        }

        // Добавляем последний ход, если строка не закончилась пустой строкой
        if (!currentMove.isEmpty()) {
            moves.add(currentMove);
        }

        return moves;
    }

    public static void main(String[] args) {
        try {
            String serverUri = "ws://localhost:8887";
            GameClient client = new GameClient(new URI(serverUri));
            client.connectBlocking();
            System.out.println("Type 'exit' to quit.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}