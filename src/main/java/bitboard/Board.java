package bitboard;

public interface Board{
     void setWhiteKingCastle(boolean whiteKingCastle);
     void setWhiteQueenCastle(boolean whiteQueenCastle);
     void setBlackKingCastle(boolean blackKingCastle);

     void setBlackQueenCastle(boolean blackQueenCastle);

     boolean isWhiteKingCastle();
     boolean isWhiteQueenCastle();
     boolean isBlackKingCastle();
     boolean isBlackQueenCastle();

     void setIsWhiteTurn(boolean isWhiteTurn);
     void decrementTimeForWhite(double time);
     void decrementTimeForBlack(double time);
     Double getTimeForWhite();
     Double getTimeForBlack();

     long getWhitePieces();
     long getBlackPieces();

     long getWhiteKingBoard();
     long getWhitePawnBoard();
     long getWhiteKnightBoard();
     long getWhiteRookBoard();
     long getWhiteBishopBoard();
     long getWhiteQueenBoard();

     long getBlackKingBoard();
     long getBlackPawnBoard();
     long getBlackKnightBoard();
     long getBlackRookBoard();
     long getBlackBishopBoard();
     long getBlackQueenBoard();

     void makeMove(int move);
     void undoMove(int move);
}
