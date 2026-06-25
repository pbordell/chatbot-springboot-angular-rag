package com.pbs.backend.service.impl;

import com.pbs.backend.service.ChatService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChatServiceImpl implements ChatService {

  @Autowired private VectorStore vectorStore;

  @Autowired private ChatModel chatModel;

  public void ingestarDocumentoManual(MultipartFile archivo) throws IOException {
    // 1. Extraemos el texto del archivo que subió el usuario
    String contenidoTexto = new String(archivo.getBytes(), StandardCharsets.UTF_8);

    // 2. Lo convertimos en un objeto Documento de Spring AI
    Document documentoOcasional = new Document(contenidoTexto);

    // 3. Lo fragmentamos de forma inteligente
    TokenTextSplitter splitter =
        TokenTextSplitter.builder()
            .withChunkSize(100)
            .withMinChunkSizeChars(50)
            .withMinChunkLengthToEmbed(5)
            .withMaxNumChunks(10000)
            .withKeepSeparator(true)
            .build();
    List<Document> fragmentos = splitter.apply(List.of(documentoOcasional));

    // 4. Lo guardamos en Pgvector
    vectorStore.add(fragmentos);
  }

  public String buscarRespuestaSemantica(String preguntaUsuario) {
    // 1. RECUPERACIÓN (R): Eliminamos el .similarityThreshold() para saltarnos el bug de Ollama con
    // Spring AI
    SearchRequest peticionBusqueda =
        SearchRequest.builder()
            .query(preguntaUsuario)
            .topK(2)
            .build();

    // 2. Ejecución de la búsqueda en la base de datos
    List<Document> documentosGanadores = vectorStore.similaritySearch(peticionBusqueda);

    // Si la base de datos está vacía porque nunca se hizo la ingesta manual
    if (documentosGanadores.isEmpty()) {
      return "Lo siento, la base de conocimiento está vacía. Por favor, sube tu archivo .txt desde el panel.";
    }

    // Concatenamos el contenido de los dos fragmentos más relevantes recuperados de Postgres
    StringBuilder contexto = new StringBuilder();
    for (Document doc : documentosGanadores) {
      contexto.append(doc.getText()).append("\n");
    }

    // 3. GENERACIÓN (G): Diseñamos el prompt definitivo para guiar el cerebro del LLM (llama3.2)
    String instruccionesSystem =
        """
            Eres un asistente virtual experto para el portafolio profesional del desarrollador.
            Tu trabajo es responder a las preguntas de los reclutadores usando ÚNICAMENTE el contexto provisto.
            Responde de forma amable, fluida, natural y muy concisa en idioma Español.

            REGLA CRUCIAL: Si la respuesta a la pregunta no se puede deducir usando el contexto de abajo, responde exactamente:
            "Lo siento, no encontré información relevante sobre esa pregunta en mi base de conocimiento."

            CONTEXTO RECUPERADO DE LA BASE DE DATOS:
            %s

            PREGUNTA DEL RECLUTADOR:
            %s
            """
            .formatted(contexto.toString(), preguntaUsuario);

    // 4. El LLM lee el currículum, procesa la pregunta y redacta la respuesta fina
    return chatModel.call(instruccionesSystem);
  }
}
