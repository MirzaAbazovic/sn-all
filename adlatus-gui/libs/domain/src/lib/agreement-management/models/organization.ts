import { TimePeriod } from './timePeriod';

export interface Organization {
  id: string;
  /**
   * Hyperlink to access the organization
   */
  href?: string;
  /**
   * If value is true, the organization is the head office
   */
  isHeadOffice?: boolean;
  /**
   * If value is true, the organization is a legal entity known by a national referential.
   */
  isLegalEntity?: boolean;
  /**
   * Organization name (department name for example)
   */
  name?: string;
  /**
   * Type of the name : Co, Inc, Ltd,…
   */
  nameType?: string;
  /**
   * Type of Organization (company, department...)
   */
  organizationType?: string;
  /**
   * Name that the organization (unit) trades under
   */
  tradingName?: string;
  existsDuring?: TimePeriod;
}
