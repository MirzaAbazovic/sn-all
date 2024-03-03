package de.bitconex.adlatus.common.infrastructure.bootstrap.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bitconex.adlatus.common.dto.Filters;
import de.bitconex.adlatus.common.util.json.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FilterInformationResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Filters.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var objectMapper = JsonUtil.createObjectMapper();
        var parameters = webRequest.getParameterMap().get("filters");
        String decodedFiltersJson = URLDecoder.decode(parameters[0], StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(decodedFiltersJson, Filters.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
