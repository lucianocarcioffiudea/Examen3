import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import controladores.TemperaturaControlador;
import datechooser.beans.DateChooserCombo;
import modelos.Temperaturas;
import servicios.TemperaturaServicios;

public class FrmTemperaturas extends JFrame {

    private DateChooserCombo dccDesde, dccHasta, dccUnica;
    private JLabel txtDesde, txtHasta, txtUnica;

    private JTabbedPane tpTemperaturas;
    private JPanel pnlGrafica;
    private JPanel pnlUnicaFecha;
    private JPanel pnlDatosProceso;

    private List<Temperaturas> datos;

    public FrmTemperaturas() {

        setTitle("Temperaturas de las capitales");
        setSize(800, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JToolBar tb = new JToolBar();

        JButton btnGraficar = new JButton();
        btnGraficar.setIcon(new ImageIcon(getClass().getResource("/iconos/Grafica.png")));
        btnGraficar.setToolTipText("Promedio Temperatura vs Rango de Fecha");
        btnGraficar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGraficarClick();
            }
        });
        tb.add(btnGraficar);

        JButton btnUnicaFecha = new JButton();
        btnUnicaFecha.setIcon(new ImageIcon(getClass().getResource("/iconos/Datos.png")));
        btnUnicaFecha.setToolTipText("Estadísticas de la ciudad seleccionada");
        btnUnicaFecha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnUnicaFechaClick();
            }
        });
        tb.add(btnUnicaFecha);

        JPanel pnlCambios = new JPanel();
        pnlCambios.setLayout(new BoxLayout(pnlCambios, BoxLayout.Y_AXIS));

        pnlDatosProceso = new JPanel();
        pnlDatosProceso.setPreferredSize(new Dimension(pnlDatosProceso.getWidth(), 50));
        pnlDatosProceso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlDatosProceso.setLayout(null);


        dccDesde = new DateChooserCombo();
        dccDesde.setBounds(250, 10, 100, 25);
        pnlDatosProceso.add(dccDesde);

        txtDesde = new JLabel("Desde:");
        txtDesde.setBounds(205, 10, 100, 25);
        pnlDatosProceso.add(txtDesde);

        dccHasta = new DateChooserCombo();
        dccHasta.setBounds(430, 10, 100, 25);
        pnlDatosProceso.add(dccHasta);

        txtHasta = new JLabel("Hasta:");
        txtHasta.setBounds(390, 10, 100, 25);
        pnlDatosProceso.add(txtHasta);

        dccUnica = new DateChooserCombo();
        dccUnica.setBounds(390, 10, 100, 25);
        pnlDatosProceso.add(dccUnica);

        txtUnica = new JLabel("Fecha especifica:");
        txtUnica.setBounds(285, 10, 120, 25);
        pnlDatosProceso.add(txtUnica);



        pnlGrafica = new JPanel();
        JScrollPane spGrafica = new JScrollPane(pnlGrafica);

        pnlUnicaFecha = new JPanel();

        tpTemperaturas = new JTabbedPane();
        tpTemperaturas.addTab("Gráfica", spGrafica);
        tpTemperaturas.addTab("Maximo y minimo de una fecha", pnlUnicaFecha);

        tpTemperaturas.addChangeListener(e -> actualizarVisibilidadFechas());

        pnlCambios.add(pnlDatosProceso);
        pnlCambios.add(tpTemperaturas);

        getContentPane().add(tb, BorderLayout.NORTH);
        getContentPane().add(pnlCambios, BorderLayout.CENTER);

        cargarDatos();

        actualizarVisibilidadFechas();
    }

    private void actualizarVisibilidadFechas() {
        boolean esGrafica = tpTemperaturas.getSelectedIndex() == 0;

        txtDesde.setVisible(esGrafica);
        dccDesde.setVisible(esGrafica);

        txtHasta.setVisible(esGrafica);
        dccHasta.setVisible(esGrafica);

        txtUnica.setVisible(!esGrafica);
        dccUnica.setVisible(!esGrafica);

        pnlDatosProceso.revalidate();
        pnlDatosProceso.repaint();
    }

    private void cargarDatos() {
        String rutaDatos = System.getProperty("user.dir") + "/src/datos/Temperaturas Capitales.csv";
        datos = TemperaturaServicios.getDatos(rutaDatos);
    }

    private void btnGraficarClick() {
        LocalDate desde = dccDesde.getSelectedDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate hasta = dccHasta.getSelectedDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        TemperaturaControlador.graficar(pnlGrafica, datos, desde, hasta);

        tpTemperaturas.setSelectedIndex(0);
    }

    private void btnUnicaFechaClick() {

        LocalDate fecha = dccUnica.getSelectedDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        TemperaturaControlador.getEstadisticas(pnlUnicaFecha, datos, fecha);

        tpTemperaturas.setSelectedIndex(1);
    }
}