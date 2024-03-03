import {ChangeDetectionStrategy, Component, ViewChild} from '@angular/core';
import {NgForOf} from '@angular/common';
import {MatListModule} from "@angular/material/list";
import {MatSidenav} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {AvatarComponent} from "@adlatus-gui/feature-components/shared/components/avatar/avatar.component";

@Component({
  selector: 'adlatus-gui-reporting-system-layout',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatListModule,
    NgForOf,
    RouterLink,
    RouterLinkActive,
    AvatarComponent,
    RouterOutlet
  ],
  templateUrl: './reporting-system-layout.component.html',
  styleUrls: ['./reporting-system-layout.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportingSystemLayoutComponent {
  appTitle: string = "Reporting System"
  mainNavigation: { link: string, title: string }[] = []
  @ViewChild("sidenav") sidenav!: MatSidenav;

  constructor(
    private route: ActivatedRoute,
  ) {

  }

  get currentYear(): number {
    return new Date().getFullYear()
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
