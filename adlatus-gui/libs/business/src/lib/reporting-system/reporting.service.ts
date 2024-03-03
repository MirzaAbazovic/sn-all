import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OrderDetails, ReportingResponse} from '@adlatus-gui/domain/reporting-system';
import {BehaviorSubject, map, Observable} from 'rxjs';
import { FilterData } from '@adlatus-gui/domain/reporting-system/models/filters';


@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  // create behavior subject
  behaviorSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  reports$: Observable<any> = this.behaviorSubject.asObservable();
  pageSize = 10;
  pageIndex = 0;
  numberOfPages = 1;
  filters: FilterData[] = [];
  active: boolean | null = null;
  constructor(private httpClient: HttpClient, @Inject('environment') private environment: Record<string, string | boolean | number>) {
  }

  updatePageSize(pageSize: number): void { // should be setter
    this.pageSize = pageSize;
  }

  updatePageIndex(pageIndex: number): void { // should be setter
    this.pageIndex = pageIndex;
  }

  changePage(pageIndex: number, pageSize: number) {
    this.updatePageIndex(pageIndex);
    this.updatePageSize(pageSize);

    this.loadReports();
  }

  changeFilter(filter: FilterData[]) {
    this.updatePageIndex(0); // we need to reset the page index to 0 when we change the filter
    this.filters = filter;

    this.loadReports();
  }

  loadActiveReports(): void {
    this.active = true;
    this.loadReports();
  }
  loadArchivedReports(): void {
    this.active = false
    this.loadReports();
  }

  getOrderDetails(orderNumber: string): Observable<OrderDetails> {
    return this.httpClient.get<OrderDetails>(`${this.environment.ORDER_DETAILS_ENDPOINT}/${orderNumber}`).pipe(
      map((orderDetails) => {
          const orderMessages = orderDetails.orderMessageInfo.map((messageInfo) => {
            return {
              ...messageInfo,
              arrivalTime: new Date(messageInfo.arrivalTime)
            };
          }).sort((a, b) => a.arrivalTime.getTime() - b.arrivalTime.getTime());

          return {
            ...orderDetails,
            report: {
              ...orderDetails.report,
              orderDate: new Date(orderDetails.report.orderDate),
              expectedCompletionDate: orderDetails.report.expectedCompletionDate ? new Date(orderDetails.report.expectedCompletionDate) : undefined
            },
            orderMessageInfo: orderMessages
          }
        }
      ));
  }

  private getUrlParams(params: { name: string, value: any }[]): string {
    return '?' + params.map(param => `${param.name}=${param.value}`).join('&');
  }

  private loadReports() {
    const params = this.buildQueryData(this.filters);
    const url = this.active ? this.environment.ACTIVE_REPORTS_ENDPOINT : this.environment.ARCHIVED_REPORTS_ENDPOINT;
    this.httpClient.get<ReportingResponse>((<string>url) + this.getUrlParams(params)).pipe(
      map((reports) => {
        this.numberOfPages = reports.numberOfPages;
        return reports.reportingList.map((report, index) => ({
          ...report,
          id: this.pageIndex * this.pageSize + index + 1
        }))
      })
    ).subscribe(e => {
      this.behaviorSubject.next({
        report: e,
        pageSize: this.pageSize,
        pageIndex: this.pageIndex,
        numberOfPages: this.numberOfPages
      });
    });
  }

  private buildQueryData(filters: any): { name: string, value: any }[] {
    const queryData = [
      {name: 'page', value: this.pageIndex},
      {name: 'size', value: this.pageSize}
    ]

    if (filters) {
      const appliedFilters = filters.filter((filter: any) => filter.value !== null && filter.value !== undefined && filter.value !== '');
      queryData.push(...appliedFilters.map((filter: any) => ({name: filter.name, value: filter.value})));
    }

    return queryData;
  }

}
