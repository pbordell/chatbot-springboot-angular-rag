import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../services/chat.service';

interface Mensaje {
  texto: string;
  esUsuario: boolean;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent {

  historialMensajes: Mensaje[] = [
    { texto: '¡Hola! Soy tu asistente. ¿En qué puedo ayudarte hoy?', esUsuario: false }
  ];
  mensajeActual: string = '';
  cargandoBot: boolean = false;

  // Variables para la Ingesta
  archivoSeleccionado: File | null = null;
  mensajeIngesta: string = '';
  cargandoIngesta: boolean = false;

  constructor(private chatService: ChatService) {}

  // --- LÓGICA DEL CHAT ---
  enviarPregunta() {
    if (!this.mensajeActual.trim() || this.cargandoBot) return;

    const textoUsuario = this.mensajeActual;
    this.historialMensajes.push({ texto: textoUsuario, esUsuario: true });
    this.mensajeActual = '';
    this.cargandoBot = true;

    this.chatService.enviarMensaje(textoUsuario).subscribe({
      next: (respuesta) => {
        this.historialMensajes.push({ texto: respuesta, esUsuario: false });
        this.cargandoBot = false;
      },
      error: (err) => {
        this.historialMensajes.push({
          texto: '❌ Error al conectar con el servidor de IA. Asegúrate de que el backend está corriendo.',
          esUsuario: false
        });
        this.cargandoBot = false;
      }
    });
  }

  // --- LÓGICA DE LA INGESTA ---
  onArchivoSeleccionado(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      if (file.type !== 'text/plain' && !file.name.endsWith('.txt')) {
        this.mensajeIngesta = '❌ Error: Solo se permiten archivos de texto plano (.txt)';
        this.archivoSeleccionado = null;
        return;
      }
      this.archivoSeleccionado = file;
      this.mensajeIngesta = `Archivo listo: ${file.name}`;
    }
  }

  ejecutarIngesta() {
    if (!this.archivoSeleccionado) {
      this.mensajeIngesta = '⚠️ Por favor, selecciona primero un archivo .txt';
      return;
    }

    this.cargandoIngesta = true;
    this.mensajeIngesta = '⚡ Procesando y vectorizando conocimiento...';

    this.chatService.subirDocumento(this.archivoSeleccionado).subscribe({
      next: (respuesta) => {
        this.mensajeIngesta = `✅ ${respuesta}`;
        this.archivoSeleccionado = null;
        this.cargandoIngesta = false;
      },
      error: (err) => {
        this.mensajeIngesta = `❌ Error en la ingesta: ${err.error || 'No se pudo procesar el archivo.'}`;
        this.cargandoIngesta = false;
      }
    });
  }
}
