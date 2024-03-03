import {IndividualCreate} from "@adlatus-gui/domain/party-management";

const emptyIndividualCreate: IndividualCreate = {
  givenName: '',
  familyName: ''
}

export const getEmptyIndividualCreate = (): IndividualCreate => {
  return Object.assign({}, emptyIndividualCreate);
}
