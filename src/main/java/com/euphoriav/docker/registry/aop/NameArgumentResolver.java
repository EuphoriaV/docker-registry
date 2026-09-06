package com.euphoriav.docker.registry.aop;

import com.euphoriav.docker.registry.aop.annotation.Name;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.logic.helper.RequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class NameArgumentResolver implements HandlerMethodArgumentResolver {

    private final RequestValidator requestValidator;

    private static final List<Pattern> PATH_PATTERNS = List.of(
            Pattern.compile("^/v2/(.+)/blobs/uploads/$"),
            Pattern.compile("^/v2/(.+)/blobs/uploads/.+$"),
            Pattern.compile("^/v2/(.+)/blobs/.+$"),
            Pattern.compile("^/v2/(.+)/manifests/.+$")
    );

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Name.class) && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        var requestPath = webRequest.getNativeRequest(HttpServletRequest.class).getRequestURI();
        for (var pattern : PATH_PATTERNS) {
            var matcher = pattern.matcher(requestPath);
            if (matcher.matches()) {
                var name = matcher.group(1);
                if (!requestValidator.validateName(name)) {
                    throw new InvalidRequestException("invalid repository name", ErrorResponse.ErrorCode.NAME_INVALID);
                }
                return name;
            }
        }
        return null;
    }
}
