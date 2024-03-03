import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {NgIf, NgStyle} from "@angular/common";

@Component({
  selector: 'adlatus-gui-avatar',
  standalone: true,
  imports: [
    NgIf,
    NgStyle
  ],
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvatarComponent {
  @Input() avatarUrl: string | undefined;
  @Input() username: string = "";
  @Input() size: number = 50; // default size is 50px

  get firstLetter(): string {
    return this.username ? this.username[0].toUpperCase() : '';
  }
}
