import {ResourceSpecificationCreate} from "@adlatus-gui/domain/resource-catalog-management";

const emptyResourceSpecificationCreate: ResourceSpecificationCreate = {
  name: ''
}

export const getEmptyResourceSpecificationCreate = () => {
  return {...emptyResourceSpecificationCreate}
}
