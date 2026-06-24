package com.pbs.backend.service.impl;

import com.pbs.backend.service.ChatService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChatServiceImpl implements ChatService {

  @Autowired private VectorStore vectorStore;

  @Value("classpath:conocimiento.txt")
  private Resource archivoConocimiento;

  public void ingestarDocumentoManual(MultipartFile archivo) throws IOException {
    // 1. Extraemos el texto del archivo que subió el usuario
    String contenidoTexto = new String(archivo.getBytes(), StandardCharsets.UTF_8);

    // 2. Lo convertimos en un objeto Documento de Spring AI
    Document documentoOcasional = new Document(contenidoTexto);

    // 3. Lo fragmentamos de forma inteligente
    TokenTextSplitter splitter =
        TokenTextSplitter.builder()
            .withChunkSize(800) // Tamaño objetivo del fragmento en tokens
            .withMinChunkSizeChars(350) // Tamaño mínimo en caracteres
            .withMinChunkLengthToEmbed(5) // Longitud mínima para ser vectorizado
            .withMaxNumChunks(10000) // Límite máximo de fragmentos a generar
            .withKeepSeparator(true) // Mantener separadores (ej: saltos de línea)
            .build();
    List<Document> fragmentos = splitter.apply(List.of(documentoOcasional));

    // 4. Lo guardamos en Pgvector
    vectorStore.add(fragmentos);
  }

  public String buscarRespuestaSemantica(String preguntaUsuario) {
    // 1. Construcción de la petición
    SearchRequest peticionBusqueda =
        SearchRequest.builder()
            .query(preguntaUsuario) // La pregunta del usuario
            .topK(1) // Traer solo el fragmento más relevante
            .similarityThreshold(0.7) // FILTRO CRUCIAL: Umbral de similitud (70%)
            .build();

    // 2. Ejecución de la búsqueda semántica
    List<Document> documentosGanadores = vectorStore.similaritySearch(peticionBusqueda);

    // 3. Extracción del resultado con seguridad
    if (!documentosGanadores.isEmpty()) {
      return documentosGanadores.get(0).getText();
    }

    return "Lo siento, no encontré información relevante sobre esa pregunta en mi base de conocimiento.";
  }
}
