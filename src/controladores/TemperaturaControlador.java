package controladores;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import modelos.Temperaturas;

public class TemperaturaControlador {

    public static void graficar(JPanel pnlGrafica,
            List<Temperaturas> datos,
            LocalDate desde, LocalDate hasta) {

        var promedios = datos.stream()

                .filter(d -> !d.getFecha().isBefore(desde) && !d.getFecha().isAfter(hasta))
                .collect(Collectors.groupingBy(
                        Temperaturas::getCiudad,
                        Collectors.averagingDouble(Temperaturas::getValor)));

        if (promedios.isEmpty()) {

            pnlGrafica.removeAll();
            pnlGrafica.revalidate();
            pnlGrafica.repaint();

            JOptionPane.showMessageDialog(
                    pnlGrafica,
                    "No hay datos en el rango de fechas seleccionado.",
                    "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        promedios.forEach((ciudad, promedio) -> {
            dataset.addValue(promedio, "Temperatura", ciudad);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Promedio de temperatura",
                "Ciudad",
                "Temperatura",
                dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(500, 300));

        pnlGrafica.removeAll();
        pnlGrafica.setLayout(new BorderLayout());
        pnlGrafica.add(panel, BorderLayout.CENTER);
        pnlGrafica.revalidate();
    }

    public static void getEstadisticas(JPanel pnl,
            List<Temperaturas> datos,
            LocalDate fecha) {
        var filtrados = datos.stream()
                .filter(d -> d.getFecha().equals(fecha))
                .toList();

        if (filtrados.isEmpty()) {

            pnl.removeAll();
            pnl.revalidate();
            pnl.repaint();

            JOptionPane.showMessageDialog(
                    pnl,
                    "No hay datos para la fecha seleccionada.",
                    "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        var max = filtrados.stream()
                .max(Comparator.comparingDouble(Temperaturas::getValor));
        var min = filtrados.stream()
                .min(Comparator.comparingDouble(Temperaturas::getValor));
        pnl.removeAll();
        pnl.setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.gridy = 0;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (max.isPresent()) {
            dataset.addValue(max.get().getValor(), "Temperatura", max.get().getCiudad());
        }

        if (min.isPresent()) {
            dataset.addValue(min.get().getValor(), "Temperatura", min.get().getCiudad());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Capitales con la mayor y menor temperatura",
                "Ciudad",
                "Temperatura",
                dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator());

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.20);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        pnl.add(chartPanel, gbc);

        pnl.revalidate();
    }
}
