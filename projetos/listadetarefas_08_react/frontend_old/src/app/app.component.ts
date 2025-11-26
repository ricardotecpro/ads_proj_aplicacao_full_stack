import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar'; // Importe aqui

@Component({
  selector: 'app-root',
  standalone: true, // Marcado como standalone
  imports: [
    RouterOutlet,
    MatToolbarModule // Adicione o módulo aqui
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'webapp';
}
