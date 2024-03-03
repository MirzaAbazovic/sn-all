import {ChangeDetectionStrategy, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MatSidenav, MatSidenavModule} from "@angular/material/sidenav";
import {ActivatedRoute, RouterLink, RouterOutlet} from "@angular/router";
import {MatListModule} from "@angular/material/list";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'adlatus-gui-admin-layout',
  standalone: true,
  imports: [
    MatSidenavModule,
    RouterOutlet,
    MatListModule,
    MatExpansionModule,
    RouterLink,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    NgForOf
  ],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminLayoutComponent implements OnInit {
  appTitle: string = "Adlatus GUI"
  mainNavigation: { link: string, title: string }[] = []
  @ViewChild("sidenav") sidenav!: MatSidenav;

  constructor(
    private route: ActivatedRoute,
  ) {

  }

  ngOnInit(): void {
    const {
      data:
        {
          appTitle,
          mainNavigation
        }
    } = this.route.snapshot
    this.appTitle = appTitle
    this.mainNavigation = mainNavigation
  }
}
