package Controller.Loader;

import javafx.scene.image.Image;

import java.util.Objects;

public class ImageLoader {
    public static Image whitePawnImg;
    public static Image whiteRookImg;
    public static Image whiteKnightImg;
    public static Image whiteBishopImg;
    public static Image whiteQueenImg;
    public static Image whiteKingImg;
    public static Image blackPawnImg;
    public static Image blackRookImg;
    public static Image blackKnightImg;
    public static Image blackBishopImg;
    public static Image blackQueenImg;
    public static Image blackKingImg;



    public static void loadPieceImages() {
        // Load images from the resources folder
        try {
            whitePawnImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_pawn.png")));
            whiteRookImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_rook.png")));
            whiteKnightImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_knight.png")));
            whiteBishopImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_bishop.png")));
            whiteQueenImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_queen.png")));
            whiteKingImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/white_king.png")));

            blackPawnImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_pawn.png")));
            blackRookImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_rook.png")));
            blackKnightImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_knight.png")));
            blackBishopImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_bishop.png")));
            blackQueenImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_queen.png")));
            blackKingImg = new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/black_king.png")));
        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }
    
    static{
        loadPieceImages();
    }
    
}
