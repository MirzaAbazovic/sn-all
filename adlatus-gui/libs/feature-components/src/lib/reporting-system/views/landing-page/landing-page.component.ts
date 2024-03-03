import {ChangeDetectionStrategy, Component} from '@angular/core';

@Component({
  selector: 'adlatus-gui-landing-page',
  standalone: true,
  imports: [],
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingPageComponent {}
