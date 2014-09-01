package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {

    private WritableImage image;

    @FXML
    private Canvas imageView;

    @FXML
    private ColorPicker colorPicker;

    private void drawImage() {
        GraphicsContext g = imageView.getGraphicsContext2D();
        g.drawImage(image, 0, 0);
    }

    @FXML
    public void initialize() {
        image = new WritableImage(300, 275);
        PixelWriter writer = image.getPixelWriter();
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                writer.setColor(x, y, Color.WHITE);
            }
        }
        drawImage();
    }

    @FXML
    public void useActiveTool(MouseEvent evt) {
        // collect input
        PixelWriter writer = image.getPixelWriter();
        int x = (int)evt.getX();
        int y = (int)evt.getY();
        Color selectedColor = colorPicker.getValue();

        // jfx drag can go beyond canvas edges, see iss #2
        if(x < 0 || y < 0 || x > image.getWidth() || y > image.getHeight()) {
            return;
        }

        // apply input
        writer.setColor(x, y, selectedColor);
        drawImage();
    }

    @FXML
    public void saveImage(ActionEvent evt) throws IOException {
        System.out.println("Saving...");
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        File outFile = new File("image.png");
        ImageIO.write(bufferedImage, "png", outFile);
        System.out.println("Wrote " + outFile);
    }

}