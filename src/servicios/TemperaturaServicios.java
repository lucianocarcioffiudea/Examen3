package servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import modelos.Temperaturas;

public class TemperaturaServicios {
    public static List<Temperaturas> getDatos(String nombreArchivo) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");

        try (var lineas = Files.lines(Paths.get(nombreArchivo))) {

            return lineas
                    .skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new Temperaturas(
                            textos[0],
                            LocalDate.parse(textos[1], formatoFecha),
                            Double.parseDouble(textos[2])))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace(); // mejor que ocultar el error
            return Collections.emptyList();
        }
    }

    public static List<Temperaturas> filtrarPorFecha(List<Temperaturas> datos, LocalDate desde, LocalDate hasta) {

        return datos.stream()
                .filter(d -> !d.getFecha().isBefore(desde) && !d.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    public static Map<String, Double> promedioPorCiudad(
            List<Temperaturas> datos) {

        return datos.stream()
                .collect(Collectors.groupingBy(
                        Temperaturas::getCiudad,
                        Collectors.averagingDouble(Temperaturas::getValor)));
    }

}
