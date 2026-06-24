package com.pbs.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pbs.backend.service.impl.ChatServiceImpl;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock private VectorStore vectorStore;

  @InjectMocks private ChatServiceImpl chatbotServiceImpl;

  @Test
  @DisplayName("Debe devolver el texto del documento cuando hay coincidencia en el Vector Store")
  void buscarRespuestaSemantica_ConCoincidencia_DevuelveTexto() {
    // Arrange
    String pregunta = "¿Cuál es tu email?";
    String textoEsperado = "Mi correo es portfolio@email.com";
    Document documentoSimulado = new Document(textoEsperado);

    when(vectorStore.similaritySearch(any(SearchRequest.class)))
        .thenReturn(List.of(documentoSimulado));

    // Act
    String respuesta = chatbotServiceImpl.buscarRespuestaSemantica(pregunta);

    // Assert
    assertEquals(textoEsperado, respuesta);
    verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
  }

  @Test
  @DisplayName(
      "Debe devolver un mensaje por defecto si el Vector Store está vacío o no supera el umbral")
  void buscarRespuestaSemantica_SinCoincidencia_DevuelveMensajePorDefecto() {
    // Arrange
    when(vectorStore.similaritySearch(any(SearchRequest.class)))
        .thenReturn(Collections.emptyList());

    // Act
    String respuesta = chatbotServiceImpl.buscarRespuestaSemantica("¿Cómo se cocina una lasaña?");

    // Assert
    assertEquals(
        "Lo siento, no encontré información relevante sobre esa pregunta en mi base de conocimiento.",
        respuesta);
  }
}
