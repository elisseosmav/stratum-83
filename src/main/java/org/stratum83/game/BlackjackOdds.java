package org.stratum83.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BlackjackOdds {
    public record Estimate(double hitBustRisk, double standWinChance, int simulations) {
    }

    private BlackjackOdds() {
    }

    public static Estimate estimate(BlackjackGame game, int simulations, Random random) {
        if (game.outcome() != BlackjackGame.Outcome.IN_PROGRESS) {
            throw new IllegalArgumentException("Odds are only estimated during a hand");
        }
        if (simulations < 1) throw new IllegalArgumentException("Simulations must be positive");
        Objects.requireNonNull(random);

        List<BlackjackGame.Card> unseen = new ArrayList<>(BlackjackGame.standardDeck());
        for (BlackjackGame.Card known : game.publicCards()) {
            if (!unseen.remove(known)) {
                throw new IllegalArgumentException("Invalid visible cards");
            }
        }

        int playerTotal = game.playerValue();
        int bust = 0;
        int winsIfStand = 0;

        for (int trial = 0; trial < simulations; trial++) {
            BlackjackGame.Card next = unseen.get(random.nextInt(unseen.size()));
            List<BlackjackGame.Card> hitHand = new ArrayList<>(game.playerCards());
            hitHand.add(next);
            if (BlackjackGame.handValue(hitHand) > 21) bust++;

            List<BlackjackGame.Card> available = new ArrayList<>(unseen);
            BlackjackGame.Card upcard = game.dealerUpcard();
            BlackjackGame.Card hole;
            do {
                hole = available.get(random.nextInt(available.size()));
            } while (BlackjackGame.handValue(List.of(upcard, hole)) == 21);
            available.remove(hole);

            List<BlackjackGame.Card> dealer = new ArrayList<>(List.of(upcard, hole));
            while (BlackjackGame.handValue(dealer) < 17) {
                BlackjackGame.Card draw = available.remove(random.nextInt(available.size()));
                dealer.add(draw);
            }
            int dealerTotal = BlackjackGame.handValue(dealer);
            if (dealerTotal > 21 || playerTotal > dealerTotal) winsIfStand++;
        }

        return new Estimate((double) bust / simulations,
                (double) winsIfStand / simulations, simulations);
    }
}
