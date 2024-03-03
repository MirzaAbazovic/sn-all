export interface Reporting {
  orderName: string;
  orderNumber: string;
  orderDate: Date;
  expectedCompletionDate?: Date;
  status: string;
  messageInterface: string;
  lineID: string;
}

export interface ReportingResponse {
  numberOfPages: number;
  reportingList: Reporting[];
}

export enum MessageDirection {
  SENT = "SENT",
  RECEIVED = "RECEIVED"
}

export interface OrderMessageInfo {
  messageType: string;
  arrivalTime: Date;
  message: string;
  messageInterface: string;
  messageDirection: MessageDirection;
}

export interface OrderDetails {
  report: Reporting;
  orderMessageInfo: OrderMessageInfo[];
}

