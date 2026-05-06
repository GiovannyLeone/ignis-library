package com.papirotech.biblioteca.dto.response;
import java.util.List;
public record PageResponse<T>(
    List<T> conteudo, int pagina, int tamanhoPagina,
    long totalElementos, int totalPaginas, boolean ultima
) {}
