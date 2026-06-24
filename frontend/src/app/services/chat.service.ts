import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private baseUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient) { }

  // 1. Enviar mensaje del usuario (GET)
  enviarMensaje(mensaje: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/preguntar`, {
      params: { mensaje: mensaje },
      responseType: 'text'
    });
  }

  // 2. Subir el documento .txt para la ingesta (POST con FormData)
  subirDocumento(archivo: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', archivo);

    return this.http.post(`${this.baseUrl}/ingestar-documento`, formData, {
      responseType: 'text'
    });
  }
}
