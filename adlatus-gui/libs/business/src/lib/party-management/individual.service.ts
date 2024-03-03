import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Individual, IndividualCreate, IndividualUpdate} from "@adlatus-gui/domain/party-management";

@Injectable({
  providedIn: 'root'
})
export class IndividualService {

  constructor(private httpClient: HttpClient, @Inject('environment') private environment: Record<string, string | boolean | number>) {
  }

  createIndividual(individualCreate: IndividualCreate) {
      return this.httpClient.post<Individual>((<string>this.environment.INDIVIDUAL_ENDPOINT), individualCreate);
  }

  retrieveAllIndividuals() {
    return this.httpClient.get<Individual[]>((<string>this.environment.INDIVIDUAL_ENDPOINT));
  }

  retrieveIndividual(id: string) {
    return this.httpClient.get<Individual>(`${(<string>this.environment.INDIVIDUAL_ENDPOINT)}/${id}`);
  }

  patchIndividual(id: string, individualUpdate: IndividualUpdate) {
    return this.httpClient.patch<Individual>(`${(<string>this.environment.INDIVIDUAL_ENDPOINT)}/${id}`, individualUpdate)
  }

  deleteIndividual(id: string) {
    return this.httpClient.delete<void>(`${(<string>this.environment.INDIVIDUAL_ENDPOINT)}/${id}`);
  }
}
