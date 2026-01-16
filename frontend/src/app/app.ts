import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],  // needed for <router-outlet>
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {}
