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
