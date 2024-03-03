package de.bitconex.adlatus.reporting.config.web;

import de.bitconex.adlatus.reporting.models.Filters;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FilterInformationResolver implements HandlerMethodArgumentResolver {
    private final List<String> allFilters = List.of("orderId", "orderDate", "fromOrderDate", "toOrderDate", "lineId");

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Filters.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var parameters = webRequest.getParameterMap().entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue()[0])).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var filteredParameters = parameters.entrySet().stream()
            .filter(entry -> allFilters.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return Filters.builder()
            .orderId(filteredParameters.get("orderId"))
            .fromOrderDate(filteredParameters.get("fromOrderDate") != null ? LocalDate.parse(filteredParameters.get("fromOrderDate")) : null)
            .toOrderDate(filteredParameters.get("toOrderDate") != null ? LocalDate.parse(filteredParameters.get("toOrderDate")) : null)
            .lineId(filteredParameters.get("lineId"))
            .build();
    }
}
