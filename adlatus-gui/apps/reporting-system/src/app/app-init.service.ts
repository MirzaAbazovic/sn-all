import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class AppInitService {
  constructor() {
  }

  initApp(): Promise<any> {
    return new Promise((resolve, reject) => {
      // get url path
      const url = window.location.pathname;

      const splitUrl = url.split('/');
      //
      // switch(splitUrl[0]) {
      //   case 'orders':
      //     of('9e3b3fb5-3420-4938-841c-62bce').subscribe((data) => {
      //       console.log(data)
      //       resolve(true);
      //     });
      //     break;
      //
      //     default:
      //       resolve(true);
      //       break;
      // }
      resolve(true);
    });
  }
}
