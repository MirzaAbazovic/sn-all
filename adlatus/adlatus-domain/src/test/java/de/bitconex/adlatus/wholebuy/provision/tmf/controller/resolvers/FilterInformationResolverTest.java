package de.bitconex.adlatus.wholebuy.provision.tmf.controller.resolvers;

import de.bitconex.adlatus.common.infrastructure.bootstrap.filters.FilterInformationResolver;
import de.bitconex.adlatus.common.dto.Filters;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterInformationResolverTest {
    private FilterInformationResolver filterInformationResolver;
    private NativeWebRequest nativeWebRequest;
    private WebDataBinderFactory webDataBinderFactory;
    private ModelAndViewContainer modelAndViewContainer;
    private MethodParameter methodParameter;

    @BeforeEach
    void setUp() {
        filterInformationResolver = new FilterInformationResolver();
        nativeWebRequest = mock(NativeWebRequest.class);
        webDataBinderFactory = mock(WebDataBinderFactory.class);
        modelAndViewContainer = mock(ModelAndViewContainer.class);
        methodParameter = mock(MethodParameter.class);
    }

    @Test
    void supportsParameter() {
        Class<?> parameterType = Filters.class;
        OngoingStubbing<Class<?>> ongoing = when(methodParameter.getParameterType());
        ongoing.thenReturn(parameterType);
        assertThat(filterInformationResolver.supportsParameter(methodParameter)).isTrue();
    }

    @Test
    void notSupportedParameter() {
        Class<?> parameterType = FilterInformationResolverTest.class;
        OngoingStubbing<Class<?>> ongoing = when(methodParameter.getParameterType());
        ongoing.thenReturn(parameterType);
        assertThat(filterInformationResolver.supportsParameter(methodParameter)).isFalse();
    }

    @Test
    void resolveArgument() {
        String jsonFilters = "{\"fromOrderDate\":\"2022-11-11\",\"toOrderDate\":\"2023-11-11\"}";
        when(nativeWebRequest.getParameterMap()).thenReturn(Map.of("filters", new String[]{jsonFilters}));
        OngoingStubbing<Class<?>> ongoing = when(methodParameter.getParameterType());
        ongoing.thenReturn(Filters.class);
        Filters filters = (Filters) filterInformationResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);

        assertThat(LocalDate.parse("2022-11-11")).isEqualTo(filters.getFromOrderDate());
        assertThat(LocalDate.parse("2023-11-11")).isEqualTo(filters.getToOrderDate());
    }
}