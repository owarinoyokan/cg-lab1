import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static final double pi = Math.PI;
    static final int width = 200;
    static final int height = 200;
    public static void main(String[] args) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int count = 8;
        for (int k = 0; k < count; k++) {
            int x0 = 100 ,y0 = 100;
            int x1,y1;
            x1 =(int)(100 + 95*Math.cos(((2*pi)/count) * k));
            y1 = (int)(100 + 95*Math.sin(((2*pi)/count) * k));
            dottedLine(image,x0,y0,x1,y1, 13,0x7FF12F);
            dottedLineFixed(image,x0,y0,x1,y1, 0x7FF12F);
            xLoopLine(image,x0,y0,x1,y1,0xFFFFFF);
            xLoopLineFixed(image,x0,y0,x1,y1,0xFFFFFF);
            xLoopLineSteep(image,x0,y0,x1,y1,0xFFFFFF);
            drawLine(image,x0,y0,x1,y1,0xFFFFFF);
        }

        File save = new File("result.png");
        ImageIO.write(image, "png", save);
    }

    // рисовка линий
    public static void drawLine(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
        boolean xchange = Math.abs((y1 - y0)) > Math.abs((x1 - x0));
        if (xchange) {
            int t;
            t = x0; x0 = y0; y0 = t;
            t = x1; x1 = y1; y1 = t;
        }

        if (x0>x1) {
            int t;
            t = x0; x0 = x1; x1 = t;
            t = y0; y0 = y1; y1 = t;
        }

        int y = y0;
        double dy = 2.0 * Math.abs(y1-y0);
        double derror = 0.0;
        int y_update = y1 > y0 ? 1 : -1;

        for (int x = x0; x < x1; x++) {
            if (xchange)
                image.setRGB(x, y, color);
            else
                image.setRGB(y, x, color);
            derror += dy;
            if (derror > (x1 - x0)) {
                derror -= 2.0*(x1 - x0);
                y += y_update;
            }
        }
    }

    public static void dottedLine(BufferedImage image, int x0, int y0, int x1, int y1, int count, int color) {
        double step = 1.0 / count;
        for (double t = 0; t <= 1; t += step) {
            int x = (int) Math.round((1.0 - t) * x0 + t * x1);
            int y = (int) Math.round((1.0 - t) * y0 + t * y1);
            image.setRGB(x, y, color);
        }
    }

    public static void dottedLineFixed(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
        double count = Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
        double step = 1.0 / count;

        for (double t = 0; t <= 1; t += step) {
            int x = (int)Math.round((1.0 - t) * x0 + t * x1);
            int y = (int)Math.round((1.0 - t) * y0 + t * y1);
            image.setRGB(x, y, color);
        }
    }

    public static void xLoopLine(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
        for (int x = x0; x <= x1; x++) {
            double t = (double)(x - x0) / (x1 - x0);
            int y = (int)Math.round((1.0 - t) * y0 + t * y1);
            image.setRGB(x, y, color);
        }
    }

    public static void xLoopLineFixed(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
        if (x0 > x1) {
            int tmpX = x0; x0 = x1; x1 = tmpX;
            int tmpY = y0; y0 = y1; y1 = tmpY;
        }

        for (int x = x0; x <= x1; x++) {
            double t = (double)(x - x0) / (x1 - x0);
            int y = (int)Math.round((1.0 - t) * y0 + t * y1);
            image.setRGB(x, y, color);
        }
    }

    public static void xLoopLineSteep(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        if (steep) {
            int tmp;
            tmp = x0; x0 = y0; y0 = tmp;
            tmp = x1; x1 = y1; y1 = tmp;
        }
        if (x0 > x1) {
            int tmp;
            tmp = x0; x0 = x1; x1 = tmp;
            tmp = y0; y0 = y1; y1 = tmp;
        }

        for (int x = x0; x <= x1; x++) {
            double t = (double)(x - x0) / (x1 - x0);
            int y = (int)Math.round((1.0 - t) * y0 + t * y1);
            if (steep)
                image.setRGB(y, x, color);
            else
                image.setRGB(x, y, color);
        }
    }
}
