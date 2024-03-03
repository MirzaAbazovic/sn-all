import {OrganizationCreate} from '../models/organizationCreate';

const emptyOrganizationCreate: OrganizationCreate = {
  tradingName: ''
}

export const getEmptyOrganizationCreate = (): OrganizationCreate => {
  return Object.assign({}, emptyOrganizationCreate);
}
