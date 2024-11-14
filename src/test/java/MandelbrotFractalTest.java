import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Test;

public class MandelbrotFractalTest {
    public static final int ITERATIONS = 1000;
    public static final double ZOOM = 1.0 / 512;
    public static double TOP = -1.0;
    public static double LEFT = -2.0;

    @Test
    public void testMandelbrotFractalIteration() throws IOException, InterruptedException {
        List<Number> iterationMatrix = List.of(10, 100, 1000, 5000, 10000);
        List<Long> sequentialResults = new ArrayList<>();
        List<Long> parallelResults = new ArrayList<>();
        for (Number nIterations : iterationMatrix) {
            sequentialResults.add(MandelbrotFractal.sequential((int) nIterations, ZOOM, TOP, LEFT));
            parallelResults.add(MandelbrotFractal.parallel((int) nIterations, ZOOM, TOP, LEFT));
        }
        XYSeriesCollection dataset = getDataset(iterationMatrix, sequentialResults, parallelResults);

        JFreeChart chart = ChartFactory.createXYLineChart(
            "Зависимость времени выполнения от кол-ва итераций",
            "Кол-во итераций",
            "Время (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = chart.getXYPlot();
        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle("Зависимость времени выполнения от кол-ва итераций", new Font("Serif", Font.BOLD, 18)));
        ChartUtils.saveChartAsPNG(new File("charts/iteration_chart.png"), chart, 1080, 720);
        
        JFrame frame = new JFrame("Mandelbrot Fractal");
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        while (true) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void testMandelbrotFractalZoom() throws IOException, InterruptedException {
        List<Number> zoomMatrix = List.of(1.0 / 1024, 1.0 / 512, 1.0 / 256, 1.0 / 128, 1.0 / 64);
        List<Long> sequentialResults = new ArrayList<>();
        List<Long> parallelResults = new ArrayList<>();
        for (Number zoomValue : zoomMatrix) {
            sequentialResults.add(MandelbrotFractal.sequential(ITERATIONS, (double) zoomValue, TOP, LEFT));
            parallelResults.add(MandelbrotFractal.parallel(ITERATIONS, (double) zoomValue, TOP, LEFT));
        }
        XYSeriesCollection dataset = getDataset(zoomMatrix, sequentialResults, parallelResults);

        JFreeChart chart = ChartFactory.createXYLineChart(
            "Зависимость времени выполнения от зума прорисовки",
            "Зум",
            "Время (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = chart.getXYPlot();
        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle("Зависимость времени выполнения от зума прорисовки", new Font("Serif", Font.BOLD, 18)));
        ChartUtils.saveChartAsPNG(new File("charts/zoom_chart.png"), chart, 1080, 720);

        JFrame frame = new JFrame("Mandelbrot Fractal");
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        while (true) {
            Thread.sleep(1000);
        }
    }

    private static XYSeriesCollection getDataset(List<Number> xMatrix, List<Long> sequentialResults, List<Long> parallelResults) {
        XYSeries sequentialSeries = new XYSeries("Последовательно");
        for (int i = 0; i < sequentialResults.size(); i++) {
            sequentialSeries.add(xMatrix.get(i), sequentialResults.get(i));
        }
        XYSeries parallelSeries = new XYSeries("Параллельно");
        for (int i = 0; i < parallelResults.size(); i++) {
            parallelSeries.add(xMatrix.get(i), parallelResults.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sequentialSeries);
        dataset.addSeries(parallelSeries);
        return dataset;
    }
}
