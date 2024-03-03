const baseUrl = 'http://localhost/reporting';

export const environment = {
  ACTIVE_REPORTS_ENDPOINT: `${baseUrl}/reports/active`,
  ARCHIVED_REPORTS_ENDPOINT: `${baseUrl}/reports/archived`,
  ORDER_DETAILS_ENDPOINT: `${baseUrl}/reports/history`
};
