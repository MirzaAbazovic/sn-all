import {ResourceSpecificationUpdate} from "@adlatus-gui/domain/resource-catalog-management";

const emptyResourceSpecificationUpdate: ResourceSpecificationUpdate = {
  name: ''
}

export const getEmptyResourceSpecificationUpdate = () => {
  return {...emptyResourceSpecificationUpdate}
}
