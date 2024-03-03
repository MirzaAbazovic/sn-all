import {ChangeDetectionStrategy, Component, OnInit, ViewChild} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {MatSidenav, MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'adlatus-gui-main-layout',
  standalone: true,
  imports: [MatSidenavModule, MatListModule, MatIconModule, RouterLink, MatButtonModule, RouterOutlet, RouterLinkActive, NgIf, NgForOf],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MainLayoutComponent implements OnInit {
  secondaryNavigation: { link: string, title: string }[] = []
  @ViewChild("sidenav") sidenav!: MatSidenav;

  constructor(
    private route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    const {
      data:
        {
          secondaryNavigation
        }
    } = this.route.snapshot
    this.secondaryNavigation = secondaryNavigation
  }
}
