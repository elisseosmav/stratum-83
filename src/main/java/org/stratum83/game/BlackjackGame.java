package org.stratum83.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BlackjackGame {
    public enum Suit {
        CLUBS("♣"), DIAMONDS("♦"), HEARTS("♥"), SPADES("♠");

        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }

    public record Card(int rank, Suit suit) {
        public Card {
            if (rank < 1 || rank > 13) {
                throw new IllegalArgumentException("Rank must be between 1 and 13");
            }
            Objects.requireNonNull(suit);
        }

        public int value() {
            return rank == 1 ? 1 : Math.min(rank, 10);
        }

        public String label() {
            String face = switch (rank) {
                case 1 -> "A";
                case 11 -> "J";
                case 12 -> "Q";
                case 13 -> "K";
                default -> Integer.toString(rank);
            };
            return face + suit.symbol();
        }

        public boolean red() {
            return suit == Suit.DIAMONDS || suit == Suit.HEARTS;
        }
    }

    public enum Outcome {
        IN_PROGRESS, PLAYER_WIN, DEALER_WIN, PUSH
    }

    private final List<Card> deck;
    private final List<Card> player = new ArrayList<>();
    private final List<Card> dealer = new ArrayList<>();
    private int drawIndex;
    private Outcome outcome = Outcome.IN_PROGRESS;
    private String message = "YOUR TURN / HIT OR STAND";

    public BlackjackGame() {
        this(new Random());
    }

    public BlackjackGame(Random random) {
        this(shuffledDeck(random));
    }

    BlackjackGame(List<Card> drawOrder) {
        if (drawOrder.size() < 4) {
            throw new IllegalArgumentException("At least four cards are needed");
        }
        deck = List.copyOf(drawOrder);
        player.add(draw());
        dealer.add(draw());
        player.add(draw());
        dealer.add(draw());
        checkNaturals();
    }

    public static List<Card> standardDeck() {
        List<Card> cards = new ArrayList<>(52);
        for (Suit suit : Suit.values()) {
            for (int rank = 1; rank <= 13; rank++) {
                cards.add(new Card(rank, suit));
            }
        }
        return List.copyOf(cards);
    }

    private static List<Card> shuffledDeck(Random random) {
        List<Card> cards = new ArrayList<>(standardDeck());
        Collections.shuffle(cards, Objects.requireNonNull(random));
        return cards;
    }

    public static int handValue(List<Card> hand) {
        int total = 0;
        int aces = 0;
        for (Card card : hand) {
            total += card.value();
            if (card.rank() == 1) aces++;
        }
        if (aces > 0 && total + 10 <= 21) total += 10;
        return total;
    }

    public static boolean isSoft(List<Card> hand) {
        int total = 0;
        boolean ace = false;
        for (Card card : hand) {
            total += card.value();
            ace |= card.rank() == 1;
        }
        return ace && total + 10 <= 21;
    }

    public List<Card> playerCards() {
        return List.copyOf(player);
    }

    public List<Card> visibleDealerCards() {
        return outcome == Outcome.IN_PROGRESS
                ? List.of(dealer.getFirst()) : List.copyOf(dealer);
    }

    public Card dealerUpcard() {
        return dealer.getFirst();
    }

    public List<Card> publicCards() {
        List<Card> cards = new ArrayList<>(player);
        cards.addAll(visibleDealerCards());
        return List.copyOf(cards);
    }

    public int playerValue() {
        return handValue(player);
    }

    public int dealerValue() {
        if (outcome == Outcome.IN_PROGRESS) {
            return handValue(List.of(dealerUpcard()));
        }
        return handValue(dealer);
    }

    public Outcome outcome() {
        return outcome;
    }

    public String message() {
        return message;
    }

    public boolean hit() {
        if (outcome != Outcome.IN_PROGRESS) return false;
        player.add(draw());
        int total = handValue(player);
        if (total > 21) {
            outcome = Outcome.DEALER_WIN;
            message = "BUST / COMPUTER WINS";
        } else if (total == 21) {
            standInternal();
        } else {
            message = "YOUR TURN / HIT OR STAND";
        }
        return true;
    }

    public boolean stand() {
        if (outcome != Outcome.IN_PROGRESS) return false;
        standInternal();
        return true;
    }

    private void standInternal() {
        while (handValue(dealer) < 17) {
            dealer.add(draw());
        }
        int dealerTotal = handValue(dealer);
        int playerTotal = handValue(player);
        if (dealerTotal > 21) {
            outcome = Outcome.PLAYER_WIN;
            message = "DEALER BUST / YOU WIN";
        } else if (playerTotal > dealerTotal) {
            outcome = Outcome.PLAYER_WIN;
            message = "YOU WIN";
        } else if (playerTotal < dealerTotal) {
            outcome = Outcome.DEALER_WIN;
            message = "COMPUTER WINS";
        } else {
            outcome = Outcome.PUSH;
            message = "PUSH / SAME SCORE";
        }
    }

    private void checkNaturals() {
        boolean playerNatural = handValue(player) == 21;
        boolean dealerNatural = handValue(dealer) == 21;
        if (!playerNatural && !dealerNatural) return;
        if (playerNatural && dealerNatural) {
            outcome = Outcome.PUSH;
            message = "BOTH BLACKJACK / PUSH";
        } else if (playerNatural) {
            outcome = Outcome.PLAYER_WIN;
            message = "BLACKJACK / YOU WIN";
        } else {
            outcome = Outcome.DEALER_WIN;
            message = "DEALER BLACKJACK / COMPUTER WINS";
        }
    }

    private Card draw() {
        if (drawIndex >= deck.size()) {
            throw new IllegalStateException("No cards remaining");
        }
        return deck.get(drawIndex++);
    }
}
