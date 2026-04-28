package controladores;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
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

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        promedios.forEach((ciudad, promedio) -> {
            dataset.addValue(promedio, "Temperatura", ciudad);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Promedio de temperatura",
                "Ciudad",
                "Temperatura",
                dataset);

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
        var max = filtrados.stream()
                .max(Comparator.comparingDouble(Temperaturas::getValor));
        var min = filtrados.stream()
                .min(Comparator.comparingDouble(Temperaturas::getValor));
        pnl.removeAll();
        pnl.setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.gridy = 0;
        if (max.isPresent()) {
            gbc.gridx = 0;
            pnl.add(new JLabel("Promedio mas caluroso:"), gbc);
            gbc.gridx = 1;
            pnl.add(new JLabel(max.get().getCiudad()), gbc);
            gbc.gridy++;
        }

        if (min.isPresent()) {
            gbc.gridx = 0;
            pnl.add(new JLabel("Promedio mas frio fría:"), gbc);
            gbc.gridx = 1;
            pnl.add(new JLabel(min.get().getCiudad()), gbc);
        }

        pnl.revalidate();
    }
}
