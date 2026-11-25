package org.stratum83.chess;

import java.util.ArrayList;
import java.util.List;

public final class ChessGame {
    public enum Side {
        WHITE, BLACK;
        public Side opposite() { return this == WHITE ? BLACK : WHITE; }
    }
    public enum Result { PLAYING, WHITE_WINS, BLACK_WINS, DRAW }
    public record Move(int fromRow, int fromCol, int toRow, int toCol, char promotion) {
        public Move(int fromRow, int fromCol, int toRow, int toCol) {
            this(fromRow, fromCol, toRow, toCol, '\0');
        }
        public boolean promotes() { return promotion != '\0'; }
    }

    private char[][] board = new char[8][8];
    private Side turn = Side.WHITE;
    private Result result = Result.PLAYING;
    private boolean whiteKingSide = true, whiteQueenSide = true;
    private boolean blackKingSide = true, blackQueenSide = true;
    private int enPassantRow = -1, enPassantCol = -1;
    private int halfmoveClock = 0;
    private final List<String> history = new ArrayList<>();

    public ChessGame() {
        String[] rows = {
                "rnbqkbnr", "pppppppp", "........", "........",
                "........", "........", "PPPPPPPP", "RNBQKBNR"
        };
        setRows(rows);
        history.add(positionKey());
    }

    private ChessGame(boolean blank) { }

    public static ChessGame fromFen(String fen) {
        String[] parts = fen.trim().split("\\s+");
        if (parts.length < 4) throw new IllegalArgumentException("Incomplete FEN");
        ChessGame game = new ChessGame(true);
        String[] ranks = parts[0].split("/");
        if (ranks.length != 8) throw new IllegalArgumentException("Expected 8 ranks");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char ch : ranks[row].toCharArray()) {
                if (ch >= '1' && ch <= '8') {
                    for (int n = 0; n < ch - '0'; n++) {
                        if (col >= 8) throw new IllegalArgumentException("Rank too long");
                        game.board[row][col++] = '.';
                    }
                } else {
                    if ("KQRBNPkqrbnp".indexOf(ch) < 0 || col >= 8) {
                        throw new IllegalArgumentException("Invalid board");
                    }
                    game.board[row][col++] = ch;
                }
            }
            if (col != 8) throw new IllegalArgumentException("Rank too short");
        }
        game.turn = switch (parts[1]) {
            case "w" -> Side.WHITE;
            case "b" -> Side.BLACK;
            default -> throw new IllegalArgumentException("Invalid turn");
        };
        game.whiteKingSide = parts[2].contains("K");
        game.whiteQueenSide = parts[2].contains("Q");
        game.blackKingSide = parts[2].contains("k");
        game.blackQueenSide = parts[2].contains("q");
        if (!parts[3].equals("-")) {
            if (parts[3].length() != 2) throw new IllegalArgumentException("Invalid en passant");
            game.enPassantCol = parts[3].charAt(0) - 'a';
            game.enPassantRow = 8 - (parts[3].charAt(1) - '0');
            if (!inside(game.enPassantRow, game.enPassantCol)) {
                throw new IllegalArgumentException("Invalid en passant");
            }
        }
        if (parts.length > 4) game.halfmoveClock = Integer.parseInt(parts[4]);
        game.history.add(game.positionKey());
        game.updateResult();
        return game;
    }

    private void setRows(String[] rows) {
        for (int i = 0; i < 8; i++) board[i] = rows[i].toCharArray();
    }

    public ChessGame copy() {
        ChessGame next = new ChessGame(true);
        for (int i = 0; i < 8; i++) next.board[i] = board[i].clone();
        next.turn = turn;
        next.result = result;
        next.whiteKingSide = whiteKingSide;
        next.whiteQueenSide = whiteQueenSide;
        next.blackKingSide = blackKingSide;
        next.blackQueenSide = blackQueenSide;
        next.enPassantRow = enPassantRow;
        next.enPassantCol = enPassantCol;
        next.halfmoveClock = halfmoveClock;
        next.history.addAll(history);
        return next;
    }

    public char piece(int row, int col) { return board[row][col]; }
    public Side turn() { return turn; }
    public Result result() { return result; }
    public boolean inCheck(Side side) {
        char king = side == Side.WHITE ? 'K' : 'k';
        for (int row = 0; row < 8; row++) for (int col = 0; col < 8; col++) {
            if (board[row][col] == king) return attacked(row, col, side.opposite());
        }
        throw new IllegalStateException("Missing king: " + side);
    }

    private static boolean inside(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    private static Side sideOf(char piece) {
        return Character.isUpperCase(piece) ? Side.WHITE : Side.BLACK;
    }
    private static boolean own(char piece, Side side) {
        return piece != '.' && sideOf(piece) == side;
    }
    private boolean enemy(int row, int col, Side side) {
        return inside(row, col) && board[row][col] != '.'
                && sideOf(board[row][col]) != side
                && Character.toLowerCase(board[row][col]) != 'k';
    }

    private boolean attacked(int row, int col, Side attacker) {
        int pawnRow = row + (attacker == Side.WHITE ? 1 : -1);
        char pawn = attacker == Side.WHITE ? 'P' : 'p';
        for (int dc : new int[]{-1, 1}) {
            if (inside(pawnRow, col + dc) && board[pawnRow][col + dc] == pawn) return true;
        }
        char knight = attacker == Side.WHITE ? 'N' : 'n';
        for (int[] offset : KNIGHT) {
            int r = row + offset[0], c = col + offset[1];
            if (inside(r, c) && board[r][c] == knight) return true;
        }
        char king = attacker == Side.WHITE ? 'K' : 'k';
        for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) {
            if ((dr != 0 || dc != 0) && inside(row + dr, col + dc)
                    && board[row + dr][col + dc] == king) return true;
        }
        for (int[] direction : BISHOP) {
            if (rayAttack(row, col, direction, attacker, 'b', 'q')) return true;
        }
        for (int[] direction : ROOK) {
            if (rayAttack(row, col, direction, attacker, 'r', 'q')) return true;
        }
        return false;
    }

    private boolean rayAttack(int row, int col, int[] direction, Side attacker,
                              char pieceA, char pieceB) {
        int r = row + direction[0], c = col + direction[1];
        while (inside(r, c)) {
            char piece = board[r][c];
            if (piece != '.') {
                char type = Character.toLowerCase(piece);
                return sideOf(piece) == attacker && (type == pieceA || type == pieceB);
            }
            r += direction[0];
            c += direction[1];
        }
        return false;
    }

    private static final int[][] KNIGHT = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };
    private static final int[][] BISHOP = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private static final int[][] ROOK = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public List<Move> legalMoves() {
        if (result != Result.PLAYING) return List.of();
        return generateLegalMoves();
    }

    private List<Move> generateLegalMoves() {
        List<Move> legal = new ArrayList<>();
        Side moving = turn;
        for (int row = 0; row < 8; row++) for (int col = 0; col < 8; col++) {
            if (!own(board[row][col], moving)) continue;
            List<Move> pseudo = new ArrayList<>();
            char piece = Character.toLowerCase(board[row][col]);
            switch (piece) {
                case 'p' -> pawnMoves(row, col, moving, pseudo);
                case 'n' -> leapMoves(row, col, moving, KNIGHT, pseudo);
                case 'b' -> slideMoves(row, col, moving, BISHOP, pseudo);
                case 'r' -> slideMoves(row, col, moving, ROOK, pseudo);
                case 'q' -> {
                    slideMoves(row, col, moving, BISHOP, pseudo);
                    slideMoves(row, col, moving, ROOK, pseudo);
                }
                case 'k' -> {
                    leapMoves(row, col, moving,
                            new int[][]{{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}}, pseudo);
                    castleMoves(row, col, moving, pseudo);
                }
                default -> throw new IllegalStateException("Invalid piece");
            }
            for (Move move : pseudo) {
                ChessGame test = copy();
                test.applyUnchecked(move);
                if (!test.inCheck(moving)) legal.add(move);
            }
        }
        return List.copyOf(legal);
    }

    private void addPawnMove(int fromRow, int fromCol, int toRow, int toCol, List<Move> moves) {
        if (toRow == 0 || toRow == 7) {
            for (char promoted : new char[]{'Q', 'R', 'B', 'N'}) {
                moves.add(new Move(fromRow, fromCol, toRow, toCol, promoted));
            }
        } else {
            moves.add(new Move(fromRow, fromCol, toRow, toCol));
        }
    }

    private void pawnMoves(int row, int col, Side side, List<Move> moves) {
        int delta = side == Side.WHITE ? -1 : 1;
        int next = row + delta;
        if (inside(next, col) && board[next][col] == '.') {
            addPawnMove(row, col, next, col, moves);
            int start = side == Side.WHITE ? 6 : 1;
            int twice = row + 2 * delta;
            if (row == start && board[twice][col] == '.') {
                moves.add(new Move(row, col, twice, col));
            }
        }
        for (int dc : new int[]{-1, 1}) {
            int targetCol = col + dc;
            if (!inside(next, targetCol)) continue;
            if (enemy(next, targetCol, side)
                    || (next == enPassantRow && targetCol == enPassantCol
                        && board[row][targetCol] == (side == Side.WHITE ? 'p' : 'P'))) {
                addPawnMove(row, col, next, targetCol, moves);
            }
        }
    }

    private void leapMoves(int row, int col, Side side, int[][] offsets, List<Move> moves) {
        for (int[] offset : offsets) {
            int nextRow = row + offset[0], nextCol = col + offset[1];
            if (!inside(nextRow, nextCol)) continue;
            if (board[nextRow][nextCol] == '.' || enemy(nextRow, nextCol, side)) {
                moves.add(new Move(row, col, nextRow, nextCol));
            }
        }
    }

    private void slideMoves(int row, int col, Side side, int[][] directions, List<Move> moves) {
        for (int[] direction : directions) {
            int r = row + direction[0], c = col + direction[1];
            while (inside(r, c)) {
                if (board[r][c] == '.') {
                    moves.add(new Move(row, col, r, c));
                } else {
                    if (enemy(r, c, side)) moves.add(new Move(row, col, r, c));
                    break;
                }
                r += direction[0]; c += direction[1];
            }
        }
    }

    private void castleMoves(int row, int col, Side side, List<Move> moves) {
        int home = side == Side.WHITE ? 7 : 0;
        if (row != home || col != 4 || inCheck(side)) return;
        Side foe = side.opposite();
        char rook = side == Side.WHITE ? 'R' : 'r';
        boolean kingSide = side == Side.WHITE ? whiteKingSide : blackKingSide;
        boolean queenSide = side == Side.WHITE ? whiteQueenSide : blackQueenSide;
        if (kingSide && board[home][7] == rook && board[home][5] == '.'
                && board[home][6] == '.' && !attacked(home, 5, foe)
                && !attacked(home, 6, foe)) {
            moves.add(new Move(home, 4, home, 6));
        }
        if (queenSide && board[home][0] == rook && board[home][1] == '.'
                && board[home][2] == '.' && board[home][3] == '.'
                && !attacked(home, 3, foe) && !attacked(home, 2, foe)) {
            moves.add(new Move(home, 4, home, 2));
        }
    }

    public boolean play(Move move) {
        if (move == null || !legalMoves().contains(move)) return false;
        applyUnchecked(move);
        history.add(positionKey());
        updateResult();
        return true;
    }

    private void applyUnchecked(Move move) {
        char piece = board[move.fromRow()][move.fromCol()];
        char taken = board[move.toRow()][move.toCol()];
        boolean pawn = Character.toLowerCase(piece) == 'p';
        boolean king = Character.toLowerCase(piece) == 'k';
        boolean rook = Character.toLowerCase(piece) == 'r';
        Side mover = turn;

        if (piece == 'K') { whiteKingSide = false; whiteQueenSide = false; }
        if (piece == 'k') { blackKingSide = false; blackQueenSide = false; }
        if (rook && move.fromRow() == 7 && move.fromCol() == 0) whiteQueenSide = false;
        if (rook && move.fromRow() == 7 && move.fromCol() == 7) whiteKingSide = false;
        if (rook && move.fromRow() == 0 && move.fromCol() == 0) blackQueenSide = false;
        if (rook && move.fromRow() == 0 && move.fromCol() == 7) blackKingSide = false;
        if (taken == 'R' && move.toRow() == 7 && move.toCol() == 0) whiteQueenSide = false;
        if (taken == 'R' && move.toRow() == 7 && move.toCol() == 7) whiteKingSide = false;
        if (taken == 'r' && move.toRow() == 0 && move.toCol() == 0) blackQueenSide = false;
        if (taken == 'r' && move.toRow() == 0 && move.toCol() == 7) blackKingSide = false;

        boolean ep = pawn && move.fromCol() != move.toCol() && taken == '.';
        if (ep) {
            board[move.fromRow()][move.toCol()] = '.';
        }
        board[move.fromRow()][move.fromCol()] = '.';
        board[move.toRow()][move.toCol()] = move.promotes()
                ? (mover == Side.WHITE ? move.promotion()
                       : Character.toLowerCase(move.promotion())) : piece;

        if (king && Math.abs(move.toCol() - move.fromCol()) == 2) {
            int home = move.fromRow();
            if (move.toCol() == 6) {
                board[home][5] = board[home][7];
                board[home][7] = '.';
            } else {
                board[home][3] = board[home][0];
                board[home][0] = '.';
            }
        }
        enPassantRow = -1; enPassantCol = -1;
        if (pawn && Math.abs(move.toRow() - move.fromRow()) == 2) {
            enPassantRow = (move.toRow() + move.fromRow()) / 2;
            enPassantCol = move.fromCol();
        }
        halfmoveClock = pawn || taken != '.' || ep ? 0 : halfmoveClock + 1;
        turn = turn.opposite();
    }

    private void updateResult() {
        List<Move> legal = generateLegalMoves();
        if (legal.isEmpty()) {
            if (inCheck(turn)) {
                result = turn == Side.WHITE ? Result.BLACK_WINS : Result.WHITE_WINS;
            } else {
                result = Result.DRAW;
            }
            return;
        }
        if (halfmoveClock >= 100 || insufficientMaterial()
                || history.stream().filter(positionKey()::equals).count() >= 3) {
            result = Result.DRAW;
        } else {
            result = Result.PLAYING;
        }
    }

    private boolean insufficientMaterial() {
        int whiteMinor = 0, blackMinor = 0;
        for (char[] row : board) for (char piece : row) {
            char type = Character.toLowerCase(piece);
            if (type == 'p' || type == 'q' || type == 'r') return false;
            if (type == 'b' || type == 'n') {
                if (Character.isUpperCase(piece)) whiteMinor++;
                else blackMinor++;
            }
        }
        return whiteMinor + blackMinor <= 1;
    }

    private String positionKey() {
        StringBuilder key = new StringBuilder(80);
        for (char[] row : board) key.append(row);
        key.append(turn == Side.WHITE ? 'w' : 'b');
        if (whiteKingSide) key.append('K');
        if (whiteQueenSide) key.append('Q');
        if (blackKingSide) key.append('k');
        if (blackQueenSide) key.append('q');
        key.append('|').append(enPassantRow).append(',').append(enPassantCol);
        return key.toString();
    }
}
