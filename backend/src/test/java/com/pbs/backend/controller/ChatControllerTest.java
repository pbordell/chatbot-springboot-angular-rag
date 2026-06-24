package com.pbs.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pbs.backend.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

  private MockMvc mockMvc;

  @Mock private ChatService chatbotService;

  @InjectMocks private ChatController chatController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
  }

  @Test
  @DisplayName("Debe devolver la respuesta del bot cuando el mensaje es válido")
  void procesarPregunta_MensajeValido_DevuelveRespuesta() throws Exception {
    // Arrange
    String mensajeUsuario = "Hola";
    String respuestaEsperada = "¡Hola! ¿En qué puedo ayudarte?";
    when(chatbotService.buscarRespuestaSemantica(mensajeUsuario)).thenReturn(respuestaEsperada);

    // Act & Assert
    mockMvc
        .perform(get("/api/chat/preguntar").param("mensaje", mensajeUsuario))
        .andExpect(status().isOk())
        .andExpect(content().string(respuestaEsperada));

    verify(chatbotService, times(1)).buscarRespuestaSemantica(mensajeUsuario);
  }

  @Test
  @DisplayName("Debe devolver Bad Request cuando el mensaje está vacío")
  void procesarPregunta_MensajeVacio_DevuelveBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/chat/preguntar").param("mensaje", "   "))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("El mensaje no puede estar vacío."));

    verifyNoInteractions(chatbotService);
  }

  @Test
  @DisplayName("Debe aceptar y procesar un archivo .txt válido")
  void alimentarChatbotConArchivo_ArchivoTxtValido_DevuelveOk() throws Exception {
    // Arrange
    MockMultipartFile archivoValido =
        new MockMultipartFile(
            "file", "curriculum.txt", "text/plain", "Mi experiencia laboral en Java".getBytes());

    // Act & Assert
    mockMvc
        .perform(multipart("/api/chat/ingestar-documento").file(archivoValido))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "El archivo 'curriculum.txt' ha sido procesado y vectorizado con éxito."));

    verify(chatbotService, times(1)).ingestarDocumentoManual(archivoValido);
  }

  @Test
  @DisplayName("Debe rechazar un archivo que no sea de texto plano")
  void alimentarChatbotConArchivo_ArchivoInvalido_DevuelveBadRequest() throws Exception {
    // Arrange (Simulamos una foto PNG engañosa)
    MockMultipartFile archivoInvalido =
        new MockMultipartFile("file", "foto.png", "image/png", new byte[] {1, 2, 3});

    // Act & Assert
    mockMvc
        .perform(multipart("/api/chat/ingestar-documento").file(archivoInvalido))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(chatbotService);
  }
}
