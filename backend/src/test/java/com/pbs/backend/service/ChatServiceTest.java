package com.pbs.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.ai.chat.model.ChatModel; // 🔥 IMPORTANTE: Nuevo mock para el LLM
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock private VectorStore vectorStore;

  @Mock private ChatModel chatModel;

  @InjectMocks private ChatServiceImpl chatbotServiceImpl;

  @Test
  @DisplayName("Debe recuperar el contexto de Postgres y devolver la respuesta redactada por el ChatModel")
  void buscarRespuestaSemantica_ConCoincidencia_DevuelveTextoRedactado() {
    // Arrange
    String pregunta = "¿Cuál es tu email?";
    String contextoBaseDeDatos = "Mi correo de contacto oficial es email@example.com";
    String respuestaRedactadaPorIa = "Hola, el correo del desarrollador es email@example.com.";

    Document documentoSimulado = new Document(contextoBaseDeDatos);

    // Simulamos la respuesta de la base de datos vectorial (Postgres)
    when(vectorStore.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of(documentoSimulado));

    // Simulamos la redacción del modelo de lenguaje (Ollama/Llama)
    when(chatModel.call(anyString()))
            .thenReturn(respuestaRedactadaPorIa);

    // Act
    String respuestaFinal = chatbotServiceImpl.buscarRespuestaSemantica(pregunta);

    // Assert
    assertEquals(respuestaRedactadaPorIa, respuestaFinal);
    verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
    verify(chatModel, times(1)).call(anyString()); // Verificamos que se llamó al LLM
  }

  @Test
  @DisplayName("Debe devolver el aviso de base de conocimiento vacía si Postgres no retorna filas")
  void buscarRespuestaSemantica_SinCoincidencia_DevuelveMensajeBaseVacia() {
    // Arrange
    when(vectorStore.similaritySearch(any(SearchRequest.class)))
            .thenReturn(Collections.emptyList());

    // Act
    String respuesta = chatbotServiceImpl.buscarRespuestaSemantica("¿Cómo se cocina una lasaña?");

    // Assert
    assertEquals(
            "Lo siento, la base de conocimiento está vacía. Por favor, sube tu archivo .txt desde el panel.",
            respuesta);
    verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
    verifyNoInteractions(chatModel); // Si la BD está vacía, no se debe consumir procesamiento del LLM
  }
}
