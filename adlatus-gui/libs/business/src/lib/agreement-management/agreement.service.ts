import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Agreement, AgreementCreate, AgreementUpdate} from "@adlatus-gui/domain/agreement-management";

@Injectable({
  providedIn: 'root'
})
export class AgreementService {

  constructor(public httpClient: HttpClient, @Inject('environment') private environment: Record<string, string | boolean | number>) {
  }

  createAgreement(agreementCreate: AgreementCreate) {
    return this.httpClient.post<Agreement>((<string>this.environment.ENDPOINT), agreementCreate);
  }

  retrieveAllAgreements() {
    return this.httpClient.get<Agreement[]>((<string>this.environment.ENDPOINT));
  }

  retrieveAgreement(id: string) {
    return this.httpClient.get<Agreement>(`${(<string>this.environment.ENDPOINT)}/${id}`);
  }

  patchAgreement(id: string, agreementUpdate: AgreementUpdate) {
    return this.httpClient.patch<Agreement>(`${(<string>this.environment.ENDPOINT)}/${id}`, agreementUpdate)
  }

  deleteAgreement(id: string) {
    return this.httpClient.delete<void>(`${(<string>this.environment.ENDPOINT)}/${id}`);
  }
}
