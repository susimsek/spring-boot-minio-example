package com.spring.minio.error;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.common.AdviceTraits;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.spring.minio.config.WebMvcConfig.MEDIA_TYPE_YAML;
import static org.zalando.problem.spring.common.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.common.MediaTypes.X_PROBLEM;

@ControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExceptionTranslator implements ProblemHandling {
    
    final Environment environment;

    @SneakyThrows(HttpMediaTypeNotAcceptableException.class)
    @Override
    public Optional<MediaType> negotiate(NativeWebRequest request) {
        final HeaderContentNegotiationStrategy negotiator = new HeaderContentNegotiationStrategy();
        final List<MediaType> mediaTypes = negotiator.resolveMediaTypes(request);

        String mediaType = request.getParameter("mediaType");

        if(mediaType==null){
            return AdviceTraits.getProblemMediaType(mediaTypes);
        }

        Optional<MediaType> optionalMediaType = Optional.empty();

        switch (mediaType) {
            case "xml":
                optionalMediaType = Optional.of(MediaType.APPLICATION_XML);
                break;
            case "yaml":
                optionalMediaType = Optional.of(MEDIA_TYPE_YAML);
                break;
            case "json":
                optionalMediaType = Optional.of(PROBLEM);
                break;
            default:
                optionalMediaType = Optional.of(X_PROBLEM);
                break;
        }

        return optionalMediaType;
    }

    @Override
    public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
        if (entity == null) {
            return entity;
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }
        ProblemBuilder builder = Problem.builder()
            .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? URI.create(environment.getProperty("problem.default.type")) : problem.getType())
            .withStatus(problem.getStatus())
            .withTitle(problem.getTitle())
            .with(environment.getProperty("problem.path.key"), request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        if (problem instanceof ConstraintViolationProblem) {
            builder
                .with(environment.getProperty("problem.violations.key"), ((ConstraintViolationProblem) problem).getViolations())
                .with(environment.getProperty("problem.message.key"), environment.getProperty("problem.error.validation"));
        } else {
            builder
                .withCause(((DefaultProblem) problem).getCause())
                .withDetail(problem.getDetail())
                .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(environment.getProperty("problem.message.key")) && problem.getStatus() != null) {
                builder.with(environment.getProperty("problem.message.key"), "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
    }



    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull NativeWebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors().stream()
            .map(f -> new FieldError(f.getObjectName().replaceFirst("Dto$", ""), f.getField(), f.getCode()))
            .collect(Collectors.toList());

        Problem problem = Problem.builder()
            .withType(URI.create(environment.getProperty("problem.constraint.violation.type")))
            .withTitle("Method argument not valid")
            .withStatus(defaultConstraintViolationStatus())
            .with(environment.getProperty("problem.message.key"), environment.getProperty("problem.error.validation"))
            .with(environment.getProperty("problem.field.errors.key"), fieldErrors)
            .build();
        return create(ex, problem, request);
    }


    @Override
    public ProblemBuilder prepare(final Throwable throwable, final StatusType status, final URI type) {

            if (throwable instanceof HttpMessageConversionException) {
                return Problem.builder()
                    .withType(type)
                    .withTitle(status.getReasonPhrase())
                    .withStatus(status)
                    .withDetail("Unable to convert http message")
                    .withCause(Optional.ofNullable(throwable.getCause())
                        .filter(cause -> isCausalChainsEnabled())
                        .map(this::toProblem)
                        .orElse(null));
            }

            if (containsPackageName(throwable.getMessage())) {
                return Problem.builder()
                    .withType(type)
                    .withTitle(status.getReasonPhrase())
                    .withStatus(status)
                    .withDetail("Unexpected runtime exception")
                    .withCause(Optional.ofNullable(throwable.getCause())
                        .filter(cause -> isCausalChainsEnabled())
                        .map(this::toProblem)
                        .orElse(null));
            }

        return Problem.builder()
            .withType(type)
            .withTitle(status.getReasonPhrase())
            .withStatus(status)
            .withDetail(throwable.getMessage())
            .withCause(Optional.ofNullable(throwable.getCause())
                .filter(cause -> isCausalChainsEnabled())
                .map(this::toProblem)
                .orElse(null));
    }

    private boolean containsPackageName(String message) {
        return StringUtils.containsAny(message,environment.getProperty("problem.unexpected.error.package-list",String[].class));
    }
}