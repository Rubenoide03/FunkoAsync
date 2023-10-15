package dev.Models;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MyFunko(UUID cod, String nombre, ModeloF modelo, double precio, LocalDate fecha, LocalDateTime created_at,
                      LocalDateTime updated_at
) {


    @Override
    public String toString() {
        return "MyFunko{" +
                "cod=" + cod +
                ", nombre='" + nombre + '\'' +
                ", modelo=" + modelo +
                ", precio=" + precio +
                ", fecha=" + fecha +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
