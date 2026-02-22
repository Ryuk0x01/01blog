import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MobileNavComponent } from './shared/components/mobile-nav/mobile-nav';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MobileNavComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');

}
