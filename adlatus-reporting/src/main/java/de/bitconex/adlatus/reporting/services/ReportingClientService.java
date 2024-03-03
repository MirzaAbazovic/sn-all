package de.bitconex.adlatus.reporting.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.adlatus.reporting.models.Filters;
import de.bitconex.adlatus.reporting.models.OrderHistory;
import de.bitconex.adlatus.reporting.models.ReportingResponse;
import de.bitconex.adlatus.reporting.utils.JsonUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ReportingClientService {

    public ReportingResponse fetchReports(Filters filters, String sort, int page, int size, String url) {
        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper mapper = JsonUtil.getObjectMapper();
        String filtersJson = null;
        try {
            filtersJson = mapper.writeValueAsString(filters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("filters", filtersJson)
            .queryParam("sort", sort)
            .queryParam("page", page)
            .queryParam("size", size);

        String uri = builder.toUriString();
        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<ReportingResponse>() {
        }).getBody();
    }

    public OrderHistory fetchSingleReport(String id, String url) {
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        String uri = builder.toUriString();
        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<OrderHistory>() {
        }).getBody();
    }
}
