import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

public class MandelbrotFractal {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1056;
    private static final int[] PALETTE = {
        0x421e0f, 0x19071a, 0x09012f, 0x040449,
        0x000764, 0x0c2c8a, 0x1852b1, 0x397DD1,
        0x86b5e5, 0xd3ecf8, 0xf1e9bf, 0xf8c95f,
        0xffaa00, 0xcc8000, 0x995700, 0x6a3403
    };
    private static final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private static final JFrame frame = new JFrame("Множество Мандельброта");

    public static void main(String[] args) {
        JLabel label = new JLabel(new ImageIcon(image));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        double zoom = 1.0 / 512;
        int iterations = 10000;
        
        //sequential(10000, 1.0 / 512, -1.0, -2.0);
        parallel(iterations, zoom, -1.0, -2.0);
    }
    
    public static long sequential(int maxIterations, double zoom, double top, double left) {
        long startTime = System.currentTimeMillis();
        for (int yPixel = 0; yPixel < HEIGHT; yPixel++) {
            drawRow(yPixel, maxIterations, zoom, top, left);
        }
        long result = System.currentTimeMillis() - startTime;
        System.out.println("Прорисовка заняла " + result + "ms");
        return result;
    }
    
    public static long parallel(int maxIterations, double zoom, double top, double left) {
        long startTime = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            for (int yPixel = 0; yPixel < HEIGHT; yPixel++) {
                final int y = yPixel;
                executor.execute(() -> {
                    drawRow(y, maxIterations, zoom, top, left);
                });
            }
            executor.shutdown();
        }
        long result = System.currentTimeMillis() - startTime;
        System.out.println("Прорисовка заняла " + result + "ms");
        return result;
    }
    
    private static void drawRow(int y, int maxIterations, double zoom, double top, double left) {
        double ci = y * zoom + top;
        for (int xPixel = 0; xPixel < WIDTH; xPixel++) {
            double cr = xPixel * zoom + left;
            double zr = 0.0;
            double zi = 0.0;
            int color = 0;
            for (int iteration = 0; iteration < maxIterations; ++iteration) {
                double zr2 = zr * zr;
                double zi2 = zi * zi;
                if (zr2 + zi2 >= 4) {
                    color = PALETTE[iteration & 15];
                    break;
                }
                zi = 2.0 * zr * zi + ci;
                zr = zr2 - zi2 + cr;
            }
            image.setRGB(xPixel, y, color);
        }
        // Обновление фрейма после завершения вычислений для каждой строки
        frame.repaint();
    } 
}
