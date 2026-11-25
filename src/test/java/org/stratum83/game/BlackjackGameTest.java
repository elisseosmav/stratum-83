package org.stratum83.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class BlackjackGameTest {
    private static BlackjackGame.Card card(int rank, BlackjackGame.Suit suit) {
        return new BlackjackGame.Card(rank, suit);
    }

    @Test
    void standardDeckContainsFiftyTwoDifferentCards() {
        List<BlackjackGame.Card> deck = BlackjackGame.standardDeck();
        assertEquals(52, deck.size());
        assertEquals(52, new HashSet<>(deck).size());
        assertEquals("A♥", card(1, BlackjackGame.Suit.HEARTS).label());
        assertEquals("K♠", card(13, BlackjackGame.Suit.SPADES).label());
        assertTrue(card(12, BlackjackGame.Suit.DIAMONDS).red());
        assertEquals(10, card(13, BlackjackGame.Suit.CLUBS).value());
    }

    @Test
    void aceUsesOneOrElevenWithoutBusting() {
        var ace = card(1, BlackjackGame.Suit.CLUBS);
        var nine = card(9, BlackjackGame.Suit.HEARTS);
        assertEquals(21, BlackjackGame.handValue(List.of(ace, ace, nine)));
        assertEquals(20, BlackjackGame.handValue(List.of(ace, ace, nine, nine)));
        assertTrue(BlackjackGame.isSoft(List.of(ace, nine)));
        assertFalse(BlackjackGame.isSoft(List.of(ace, nine, nine)));
    }

    @Test
    void hidesTheDealerHoleCardUntilHandEnds() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(10, BlackjackGame.Suit.CLUBS),
                card(9, BlackjackGame.Suit.SPADES),
                card(8, BlackjackGame.Suit.HEARTS),
                card(8, BlackjackGame.Suit.DIAMONDS),
                card(6, BlackjackGame.Suit.CLUBS)));
        assertEquals(BlackjackGame.Outcome.IN_PROGRESS, game.outcome());
        assertEquals(1, game.visibleDealerCards().size());
        assertEquals(3, game.publicCards().size());
        assertEquals(9, game.dealerValue());
        assertEquals(18, game.playerValue());
        assertTrue(game.stand());
        assertEquals(BlackjackGame.Outcome.PLAYER_WIN, game.outcome());
        assertEquals(2, game.visibleDealerCards().size());
        assertEquals(17, game.dealerValue());
    }

    @Test
    void playerBlackjackBeatsARegularDealerHand() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(1, BlackjackGame.Suit.HEARTS),
                card(9, BlackjackGame.Suit.CLUBS),
                card(13, BlackjackGame.Suit.SPADES),
                card(7, BlackjackGame.Suit.DIAMONDS)));
        assertEquals(BlackjackGame.Outcome.PLAYER_WIN, game.outcome());
        assertEquals("BLACKJACK / YOU WIN", game.message());
        assertFalse(game.hit());
    }

    @Test
    void dealerBlackjackAndDoubleBlackjackAreHandled() {
        BlackjackGame dealerWins = new BlackjackGame(List.of(
                card(10, BlackjackGame.Suit.HEARTS),
                card(1, BlackjackGame.Suit.SPADES),
                card(8, BlackjackGame.Suit.CLUBS),
                card(13, BlackjackGame.Suit.DIAMONDS)));
        assertEquals(BlackjackGame.Outcome.DEALER_WIN, dealerWins.outcome());

        BlackjackGame push = new BlackjackGame(List.of(
                card(1, BlackjackGame.Suit.SPADES),
                card(1, BlackjackGame.Suit.HEARTS),
                card(13, BlackjackGame.Suit.DIAMONDS),
                card(13, BlackjackGame.Suit.CLUBS)));
        assertEquals(BlackjackGame.Outcome.PUSH, push.outcome());
    }

    @Test
    void playerHitCanBustAndEndsRound() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(10, BlackjackGame.Suit.CLUBS),
                card(6, BlackjackGame.Suit.SPADES),
                card(9, BlackjackGame.Suit.DIAMONDS),
                card(10, BlackjackGame.Suit.HEARTS),
                card(5, BlackjackGame.Suit.CLUBS)));
        assertTrue(game.hit());
        assertEquals(24, game.playerValue());
        assertEquals(BlackjackGame.Outcome.DEALER_WIN, game.outcome());
        assertFalse(game.hit());
        assertFalse(game.stand());
    }

    @Test
    void playerReachingTwentyOneAutomaticallyPlaysDealer() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(10, BlackjackGame.Suit.CLUBS),
                card(6, BlackjackGame.Suit.SPADES),
                card(7, BlackjackGame.Suit.HEARTS),
                card(10, BlackjackGame.Suit.HEARTS),
                card(4, BlackjackGame.Suit.SPADES),
                card(10, BlackjackGame.Suit.DIAMONDS)));
        assertTrue(game.hit());
        assertEquals(21, game.playerValue());
        assertEquals(BlackjackGame.Outcome.PLAYER_WIN, game.outcome());
        assertEquals(26, game.dealerValue());
    }

    @Test
    void dealerStandsOnSoftSeventeen() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(10, BlackjackGame.Suit.CLUBS),
                card(1, BlackjackGame.Suit.HEARTS),
                card(8, BlackjackGame.Suit.DIAMONDS),
                card(6, BlackjackGame.Suit.HEARTS),
                card(2, BlackjackGame.Suit.SPADES)));
        assertTrue(game.stand());
        assertEquals(17, game.dealerValue());
        assertEquals(2, game.visibleDealerCards().size());
        assertEquals(BlackjackGame.Outcome.PLAYER_WIN, game.outcome());
    }

    @Test
    void dealerHitsSoftSixteenAndPushesAtEighteen() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(10, BlackjackGame.Suit.CLUBS),
                card(1, BlackjackGame.Suit.HEARTS),
                card(8, BlackjackGame.Suit.DIAMONDS),
                card(5, BlackjackGame.Suit.HEARTS),
                card(2, BlackjackGame.Suit.SPADES)));
        assertTrue(game.stand());
        assertEquals(18, game.dealerValue());
        assertEquals(BlackjackGame.Outcome.PUSH, game.outcome());
        assertEquals(3, game.visibleDealerCards().size());
    }

    @Test
    void monteCarloEstimatesStayWithinProbabilityBounds() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(10, BlackjackGame.Suit.CLUBS),
                card(6, BlackjackGame.Suit.SPADES),
                card(8, BlackjackGame.Suit.HEARTS),
                card(7, BlackjackGame.Suit.DIAMONDS)));
        BlackjackOdds.Estimate odds = BlackjackOdds.estimate(game, 4000, new Random(83));
        assertEquals(4000, odds.simulations());
        assertTrue(odds.hitBustRisk() > .70 && odds.hitBustRisk() < .80);
        assertTrue(odds.standWinChance() >= 0 && odds.standWinChance() <= 1);
    }

    @Test
    void softHandCannotBustWithOneAdditionalCard() {
        BlackjackGame game = new BlackjackGame(List.of(
                card(1, BlackjackGame.Suit.CLUBS),
                card(9, BlackjackGame.Suit.SPADES),
                card(6, BlackjackGame.Suit.HEARTS),
                card(7, BlackjackGame.Suit.DIAMONDS)));
        BlackjackOdds.Estimate odds = BlackjackOdds.estimate(game, 1000, new Random(1));
        assertEquals(0, odds.hitBustRisk());
    }

    @Test
    void randomizedGamesAlwaysDealFourUniqueCards() {
        for (int seed = 0; seed < 20; seed++) {
            BlackjackGame game = new BlackjackGame(new Random(seed));
            assertEquals(2, game.playerCards().size());
            assertEquals(2, game.visibleDealerCards().size() + (game.outcome()
                    == BlackjackGame.Outcome.IN_PROGRESS ? 1 : 0));
            assertTrue(game.playerValue() >= 4 && game.playerValue() <= 21);
        }
    }
}
