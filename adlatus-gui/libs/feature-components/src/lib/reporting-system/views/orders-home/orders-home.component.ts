import {ChangeDetectionStrategy, Component} from '@angular/core';

@Component({
  selector: 'adlatus-gui-orders-home',
  standalone: true,
  imports: [],
  templateUrl: './orders-home.component.html',
  styleUrls: ['./orders-home.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OrdersHomeComponent {}
