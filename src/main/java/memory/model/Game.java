package memory.model;

import memory.util.Observable;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Game extends Observable<Game> {
    private Player playerOne;
    private Player playerTwo;
    private Player currentPlayer;
    private List<Card> cards = new ArrayList<>();
    private List<Card> selectedCards = new ArrayList<>();
    private final boolean CHEAT_MODE = false;

    public Game() {
        this.generatePlayers();
    }

    /**
     * Generate 36 cards and return .mem-like data to render the grid.
     */
    public List<Integer> generateCards() {
        List<Integer> data = new ArrayList<>();

        // Start pairs with + 1 to fix the offset caused by starting with 1.
        for (int id = 1; id < 18 + 1; id++) {
            data.add(id);
            data.add(id);
        }

        Collections.shuffle(data);

        return data;
    }

    /**
     * Chooses a random player to start.
     */
    public void generatePlayers() {
        Random random = new Random();

        var playerOne = new Player("Player 1");
        var playerTwo = new Player("Player 2");

        this.setplayerOne(playerOne);
        this.setplayerTwo(playerTwo);

        if (random.nextBoolean()) {
            this.currentPlayer = this.playerOne;
        } else {
            this.currentPlayer = this.playerTwo;
        }
    }

    /**
     * Calculates if the selected cards make a pair and passes the turn to the next player.
     */
    public void calculatePair() {
        var pair = this.selectedCards.get(0).getId().equals(this.selectedCards.get(1).getId());

        if (pair) this.getCurrentPlayer().addBadge(new Badge(this.getSelectedCards().get(0).getId()));

        this.processPair(pair);

        this.clearSelectedCards();

        this.setCurrentPlayer((this.getCurrentPlayer() == this.getplayerOne()) ? this.getplayerTwo() : this.getplayerOne());
    }

    /**
     * Checks if the selected cards form pair.
     * If so, add them to the players cards.
     */
    public void processPair(boolean pair) {
        this.cards.forEach(card -> {
            if (card.isShown()) {
                card.select(true);
                card.show(false);
                if (pair) card.setId(-1);
            }
        });
    }

    /**
     * Turn the data into actual card objects.
     *
     * @param data .mem-like card data.
     */
    public void loadCards(List<Integer> data) {
        List<Card> cards = new ArrayList<>();

        data.forEach(id -> {
            var decimalFormat = new DecimalFormat("00");
            cards.add(new Card(id, String.format("/images/image%s.jpg", decimalFormat.format((id == -1) ? 1 : id)), CHEAT_MODE));
        });

        this.setCards(cards);
    }

    /**
     * Check if the game is supposed to end.
     *
     * @return true if the game should end.
     */
    public boolean checkForGameEnd() {
        var playerOneBadges = this.getplayerOne().getBadges();
        var playerTwoBadges = this.getplayerTwo().getBadges();

        var allCardsUsed = playerOneBadges.size() + playerTwoBadges.size() >= 18;
        var playerOneCheatMode = this.isCheatMode() && playerOneBadges.size() >= 3;
        var playerTwoCheatMode = this.isCheatMode() && playerTwoBadges.size() >= 3;

        if (allCardsUsed) return true;
        if (playerOneCheatMode) return true;
        if (playerTwoCheatMode) return true;

        return false;
    }

    /**
     * Read a save file and return grid data based on its contents.
     *
     * @param save The save file.
     */
    public List<Integer> readSaveFileGrid(File save) throws Exception {
        List<Integer> data = new ArrayList<>();

        var scanner = new Scanner(save);

        while (scanner.hasNextInt()) {
            var id = scanner.nextInt();

            if (id > 18) throw new Exception();
            if (id < -1) throw new Exception();

            data.add(id);
        }

        if (data.size() != 36) throw new Exception();

        return data;
    }

    /**
     * Read a save file and return player data on its contents.
     *
     * @param save The save file.
     */
    public Player readSaveFilePlayer(File save, Integer playerNumber) throws Exception {
        var player = new Player("Player");

        var scanner = new Scanner(save);

        // Skip grid data.
        while (scanner.hasNextInt()) {
            scanner.nextInt();
        }
        scanner.nextLine();

        if (playerNumber == 2) {
            scanner.nextLine();
            scanner.nextLine();
        }

        var name = scanner.nextLine();

        player.setName(name);

        var line = scanner.nextLine();

        var lineScanner = new Scanner(line);

        while (lineScanner.hasNextInt()) {
            var id = lineScanner.nextInt();

            if (id > 18) throw new Exception();
            if (id < -1) throw new Exception();

            player.addBadge(new Badge(id));
        }

        lineScanner.close();

        return player;
    }

    /**
     * Reads the current player data from the given save file.
     *
     * @param save The save file.
     */
    public void readSaveFileCurrentPlayer(File save) throws Exception {
        var scanner = new Scanner(save);

        for (int i = 0; i < 10; i++) scanner.nextLine();

        var current = scanner.nextInt();

        if (current != 0 && current != 1) throw new Exception();

        if (current == 0) this.setCurrentPlayer(this.getplayerOne());
        else this.setCurrentPlayer(this.getplayerTwo());
    }

    /**
     * Writes the current game data to the provided file.
     *
     * @param file The file the data gets written to.
     */
    public void writeSaveFile(File file) {
        if (!file.getName().contains(".")) {
            file = new File(file.getAbsolutePath() + ".mem");
        }

        var cards = this.getCards();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            for (int i = 0; i < cards.size(); i++) {
                bw.write(String.format("%s ", cards.get(i).getId().toString()));
                if ((i + 1) % 6 == 0) bw.newLine();
            }

            bw.write(this.playerOne.getName().getValue());
            bw.newLine();

            for (int i = 0; i < playerOne.getBadges().size(); i++) {
                bw.write(String.format("%s ", playerOne.getBadges().get(i).getId().toString()));
            }

            bw.newLine();
            bw.write(this.playerTwo.getName().getValue());
            bw.newLine();

            for (int i = 0; i < playerTwo.getBadges().size(); i++) {
                bw.write(String.format("%s ", playerTwo.getBadges().get(i).getId().toString()));
            }

            bw.newLine();

            if (this.currentPlayer == playerOne) bw.write("0");
            else bw.write("1");
            bw.close();
        } catch (IOException ignored) {
        }
    }

    public void setplayerOne(Player player) {
        this.playerOne = player;
        this.update(this);
    }

    public void setplayerTwo(Player player) {
        this.playerTwo = player;
        this.update(this);
    }

    public Player getplayerOne() {
        return this.playerOne;
    }

    public Player getplayerTwo() {
        return this.playerTwo;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        this.update(this);
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public List<Card> getCards() {
        return List.copyOf(this.cards);
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
        this.update(this);
    }

    public List<Card> getSelectedCards() {
        return List.copyOf(this.selectedCards);
    }

    public void clearSelectedCards() {
        this.selectedCards.clear();
        this.update(this);
    }

    public void addSelectedCard(Card card) {
        this.selectedCards.add(card);
        this.update(this);
    }

    public boolean isCheatMode() {
        return CHEAT_MODE;
    }

    public String checkForWinner() {
        var winner = (this.playerOne.getBadges().size() > this.playerTwo.getBadges().size()) ? this.playerOne.getName().getValue() : this.playerTwo.getName().getValue();
        winner = (this.playerOne.getBadges().size() == this.playerTwo.getBadges().size()) ? "No one" : winner;

        return winner;
    }
}
