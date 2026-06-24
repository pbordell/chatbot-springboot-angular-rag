package com.pbs.backend.utils;

import org.springframework.web.multipart.MultipartFile;

public class FilesUtils {

  public static boolean validarFichero(MultipartFile archivo) {
    if (archivo.isEmpty()) {
      return false;
    }

    String tipoContenido = archivo.getContentType();
    if (tipoContenido == null || !tipoContenido.equals("text/plain")) {
      return false;
    }

    String nombreArchivo = archivo.getOriginalFilename();
    if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".txt")) {
      return false;
    }
    return true;
  }
}
