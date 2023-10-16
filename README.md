# FunkoAsync

Autores
-------
Ruben Fernandez

# Descripción
Es un proyecto de lectura de archivo CSV con procesamiento de datos e implementacion en bases de datos todo de manera asincrona

# Dependencias
* Java 17
* Gradle 
* Y algun IDE que soporte Java 17 y todas las dependencias

# Explicacion

## FunkoService 
* Esta es la clase FunkoSercice que es la que se encarga de leer el archivo CSV y procesar los datos
* Usamos los completables futures para que sea asincrono y no se bloquee el programa
* Creamos una lista de funkos que posteriormente guardara los datos del CSV
* Lo primero que hace es hacer un try with resources para leer el archivo CSV y lo que hace es leerlo linea a linea y lo guarda en una lista de string
* Usa el builder de Funko para crear un funko con los datos del CSV y lo guarda en la lista de funkos
* Posteriormente devuelve la lista de funkos.
* El resto de metodos son para procesar los datos de la lista de funkos y devolverlos de manera asincrona
* El metodo funkoMasCaro devuelve el funko mas caro y recorre la lista comparando los precios y devolviendo el mas caro
* El metodo precioMedio devuelve el precio medio de los funkos y recorre la lista y hace un mapToDouble para convertirlo en un double y despues hace un average para sacar la media
* El metodo funkosPorModelo devuelve un map con los funkos agrupados por modelo
* El metodo numerodeFunkosPorModelo devuelve un map con el numero de funkos agrupados por modelo
* El metodo funkosLanzados2023 devuelve una lista de funkos que se lanzaron en el 2023 comparando y recorriendo las fechas con el año 2023
* El metodo funkosStitch devuelve una lista de funkos que se 
* 
````
package dev.services;

import dev.Models.ModeloF;
import dev.Models.MyFunko;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FunkoService {
    private static FunkoService instance;
    List<MyFunko> funkos;

    {
        try {
            funkos = readAllCSV("src/main/resources/funkos.csv").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public CompletableFuture<List<MyFunko>> readAllCSV(String route_file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(route_file), StandardCharsets.UTF_8);
            List<MyFunko> funkos = new ArrayList<>();

            for (int i = 1; i < lines.size(); i++) {
                String[] lines_split = lines.get(i).split(",");
                if (lines_split.length == 5) {
                    DecimalFormat df = new DecimalFormat("#.###");
                    UUID cod = UUID.fromString(lines_split[0].substring(0, 35));
                    String nombre = lines_split[1];
                    ModeloF modelo = ModeloF.valueOf(lines_split[2]);
                    double precio = Double.parseDouble(lines_split[3].replace(',', '.'));
                    LocalDate fecha = LocalDate.parse(lines_split[4]);
                    LocalDateTime createdAt = LocalDateTime.now();
                    LocalDateTime updatedAt = LocalDateTime.now();
                    MyFunko myFunko = new MyFunko(cod, nombre, modelo, precio, fecha, createdAt, updatedAt);
                    funkos.add(myFunko);
                }
            }

            return CompletableFuture.completedFuture(funkos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public CompletableFuture<MyFunko> funkoMasCaro() {
        return CompletableFuture.supplyAsync(() -> funkos.stream().max(Comparator.comparing(MyFunko::precio)).get());
    }
    public CompletableFuture<Double> precioMedio() {
        return CompletableFuture.supplyAsync(() -> funkos.stream().mapToDouble(MyFunko::precio).average().getAsDouble());
    }
    public CompletableFuture<Map<ModeloF,List<MyFunko>>> funkosPorModelo() {
        return CompletableFuture.supplyAsync(() -> funkos.stream().collect(Collectors.groupingBy(MyFunko::modelo)));
    }
    public CompletableFuture<Map<ModeloF,Long>> numerodeFunkosPorModelo(List<MyFunko> funkos) {
        return CompletableFuture.supplyAsync(() -> funkos.stream().collect(Collectors.groupingBy(MyFunko::modelo, Collectors.counting())));
    }
    public CompletableFuture<List<MyFunko>> funkosLanzados2023(List<MyFunko> funkos) {
        return CompletableFuture.supplyAsync(() -> funkos.stream().filter(myFunko -> myFunko.fecha().getYear() == 2023).toList());
    }
    public CompletableFuture<List<MyFunko>> funkosStitch(List<MyFunko> funkos) {
        return CompletableFuture.supplyAsync(() -> funkos.stream().filter(myFunko -> myFunko.nombre().equals("Stitch")).toList());
    }
    public CompletableFuture<Long> numeroFunkosStitch(List<MyFunko> funkos) {
        return CompletableFuture.supplyAsync(() -> funkos.stream().filter(myFunko -> myFunko.nombre().equals("Stitch")).count());
    }
}

````