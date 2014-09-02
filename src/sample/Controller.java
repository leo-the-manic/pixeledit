package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Controller {

    @FXML
    private Canvas imageView;

    @FXML
    private ColorPicker colorPicker;

    // used to provide smooth drawing over several isolated drag events
    private Optional<MouseEvent> oldEvent = Optional.empty();

    private static boolean inBounds(Canvas imageView, int x, int y) {
        int w = (int) imageView.getWidth();
        int h = (int) imageView.getHeight();
        return x > 0 && y > 0 && x < w && y < h;
    }

    @FXML
    public void initialize() {
        GraphicsContext g = imageView.getGraphicsContext2D();
        g.setFill(Paint.valueOf("#FFF"));
        g.fillRect(0, 0, imageView.getWidth(), imageView.getHeight());
    }

    private void putPixel(GraphicsContext g, int x, int y) {
        g.fillRect(x, y, 1, 1);
    }

    @FXML
    public void useActiveTool(MouseEvent evt) {
        // collect input
        GraphicsContext g = imageView.getGraphicsContext2D();
        int x = (int) evt.getX();
        int y = (int) evt.getY();
        Color selectedColor = colorPicker.getValue();

        // apply input
        // jfx drag can go beyond canvas edges, see iss #2
        if (!inBounds(imageView, x, y)) {
            resetLineSmoothing();
            return;
        }

        g.setStroke(Paint.valueOf(selectedColor.toString()));
        g.setFill(Paint.valueOf(selectedColor.toString()));

        // fill in from last position if user is dragging
        if (oldEvent.isPresent()) {
            double oldX = oldEvent.get().getX();
            double oldY = oldEvent.get().getY();
            g.strokeLine(oldX, oldY, x, y);
        }
        putPixel(g, x, y);

        // remember last position if user is dragging
        if (evt.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            oldEvent = Optional.of(evt);
        }
    }

    @FXML
    public void resetLineSmoothing() {
        oldEvent = Optional.empty();
    }

    @FXML
    public void saveImage() {
        System.out.println("Saving...");
        imageView.snapshot(canvasShot -> {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(
                    canvasShot.getImage(), null);
            File outFile = new File("image.png");
            try {
                ImageIO.write(bufferedImage, "png", outFile);
                System.out.println("Wrote " + outFile);
            } catch (IOException e) {
                System.out.println("Error saving image: " + e);
            }
            return null;
        }, null, null);
    }
}