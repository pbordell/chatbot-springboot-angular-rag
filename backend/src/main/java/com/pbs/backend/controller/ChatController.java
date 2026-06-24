package com.pbs.backend.controller;

import com.pbs.backend.service.ChatService;
import java.io.IOException;

import com.pbs.backend.utils.FilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

  @Autowired private ChatService chatService;

  @PostMapping("/ingestar-documento")
  public ResponseEntity<String> ingesta(@RequestParam("file") MultipartFile archivo) {

    if (!FilesUtils.validarFichero(archivo)) {
      return ResponseEntity.badRequest().body("Por favor, selecciona un archivo .txt válido. ");
    }

    try {
      // Llamamos al servicio pasando el archivo subido
      chatService.ingestarDocumentoManual(archivo);
      return ResponseEntity.ok(
          "El archivo '"
              + archivo.getOriginalFilename()
              + "' ha sido procesado y vectorizado con éxito.");
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Error al procesar el archivo: " + e.getMessage());
    }
  }

  @GetMapping(path= "/preguntar", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> procesarPregunta(@RequestParam String mensaje) {
    if (mensaje == null || mensaje.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("El mensaje no puede estar vacío.");
    }

    String respuestaBot = chatService.buscarRespuestaSemantica(mensaje);
    System.out.println("📤 Enviando respuesta a Angular: " + respuestaBot);

    return ResponseEntity.ok(respuestaBot);
  }
}
