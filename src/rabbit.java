import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class rabbit {
    public static void main() throws IOException {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        ArrayList<float[]> vertices = new ArrayList<>();
        ArrayList<int[]> faces = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("model_1.obj"));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue; // комментарии
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "v": // вершина
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    vertices.add(new float[]{x, y, z});
                    break;
                case "f": // грань
                    int[] face = new int[parts.length - 1];
                    for (int i = 1; i < parts.length; i++) {
                        // f v/vt/vn или f v//vn
                        String[] sub = parts[i].split("/");
                        face[i - 1] = Integer.parseInt(sub[0]) - 1; // индексация с 0
                    }
                    faces.add(face);
                    break;
            }
        }

        // параметры проекции/масштаба
        float scale = 5000f;
        float offset = 500f;
        int r = (10 * 50) % 256;
        int g = (10 * 80) % 256;
        int b = (10 * 120) % 256;
        int color = (r << 16) | (g << 8) | b;

        for (int[] face : faces) {
            for (int i = 0; i < face.length; i++) {
                int i0 = face[i];
                int i1 = face[(i + 1) % face.length]; // соединяем последнюю с первой
                int x0 = (int) (vertices.get(i0)[0] * scale + offset);
                int y0 = (int) (vertices.get(i0)[1] * scale + offset);
                int x1 = (int) (vertices.get(i1)[0] * scale + offset);
                int y1 = (int) (vertices.get(i1)[1] * scale + offset);
                drawLine(img, x0, y0, x1, y1, color);
            }
        }
        img = flipImageVertically(img);

        File save = new File("model.png");
        ImageIO.write(img, "png", save);
        System.out.println("model.png");
    }

    public static BufferedImage flipImageVertically(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, original.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                flipped.setRGB(x, height - 1 - y, original.getRGB(x, y));
            }
        }

        return flipped;
    }

    public static void drawLine(BufferedImage img, int x0, int y0, int x1, int y1, int color) {
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

        int dx = x1 - x0;
        int dy = Math.abs(y1 - y0);
        int error = 0;
        int y = y0;
        int ystep = y0 < y1 ? 1 : -1;

        for (int x = x0; x <= x1; x++) {
            if (steep) {
                if (y >= 0 && y < img.getWidth() && x >= 0 && x < img.getHeight())
                    img.setRGB(y, x, color);
            } else {
                if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight())
                    img.setRGB(x, y, color);
            }
            error += dy;
            if (2 * error >= dx) {
                y += ystep;
                error -= dx;
            }
        }
    }
}
