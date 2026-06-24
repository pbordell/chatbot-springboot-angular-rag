package com.pbs.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ChatService {
  void ingestarDocumentoManual(MultipartFile archivo) throws IOException;

  String buscarRespuestaSemantica(String preguntaUsuario);
}
