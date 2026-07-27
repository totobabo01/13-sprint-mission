package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentStorageException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.InvalidMessageException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * 존재하지 않는 리소스를 조회한 경우
     * HTTP 404 Not Found
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            ChannelNotFoundException.class,
            MessageNotFoundException.class,
            BinaryContentNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            DiscodeitException exception
    ) {
        log.warn(
                "리소스를 찾을 수 없습니다. code={}, details={}",
                exception.getErrorCode(),
                exception.getDetails()
        );

        return buildResponse(
                exception,
                HttpStatus.NOT_FOUND
        );
    }

    /*
     * 사용자 이름 또는 이메일이 중복된 경우
     * HTTP 409 Conflict
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException exception
    ) {
        log.warn(
                "사용자 정보 충돌이 발생했습니다. code={}, details={}",
                exception.getErrorCode(),
                exception.getDetails()
        );

        return buildResponse(
                exception,
                HttpStatus.CONFLICT
        );
    }

    /*
     * 올바르지 않은 도메인 요청인 경우
     * HTTP 400 Bad Request
     */
    @ExceptionHandler({
            PrivateChannelUpdateException.class,
            InvalidMessageException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            DiscodeitException exception
    ) {
        log.warn(
                "잘못된 도메인 요청입니다. code={}, details={}",
                exception.getErrorCode(),
                exception.getDetails()
        );

        return buildResponse(
                exception,
                HttpStatus.BAD_REQUEST
        );
    }

    /*
     * 실제 파일 저장소 처리 중 오류가 발생한 경우
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(BinaryContentStorageException.class)
    public ResponseEntity<ErrorResponse> handleBinaryContentStorageException(
            BinaryContentStorageException exception
    ) {
        log.error(
                "파일 저장소 처리 중 오류가 발생했습니다. code={}, details={}",
                exception.getErrorCode(),
                exception.getDetails(),
                exception
        );

        return buildResponse(
                exception,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /*
     * 위 핸들러에서 구체적으로 처리하지 않은
     * DiscodeitException 계열 예외의 공통 처리
     */
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(
            DiscodeitException exception
    ) {
        log.warn(
                "Discodeit 커스텀 예외가 발생했습니다. code={}, details={}",
                exception.getErrorCode(),
                exception.getDetails()
        );

        return buildResponse(
                exception,
                HttpStatus.BAD_REQUEST
        );
    }

    /*
     * @Valid 검증 실패 처리
     *
     * 예:
     * username 필수 값 누락
     * email 형식 오류
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, Object> details = new LinkedHashMap<>();

        for (FieldError fieldError
                : exception.getBindingResult().getFieldErrors()) {

            details.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        log.warn(
                "요청 값 검증에 실패했습니다. details={}",
                details
        );

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.METHOD_ARGUMENT_NOT_VALID,
                ErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage(),
                details,
                exception.getClass(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * JSON 형식이 잘못됐거나 요청 본문을 읽을 수 없는 경우
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        log.warn(
                "요청 본문을 읽을 수 없습니다. message={}",
                exception.getMessage()
        );

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.MESSAGE_NOT_READABLE,
                ErrorCode.MESSAGE_NOT_READABLE.getMessage(),
                Map.of(),
                exception.getClass(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * 경로 변수 또는 요청 파라미터의 타입 변환에 실패한 경우
     *
     * 예:
     * UUID가 필요한 자리에 abc 입력
     * 숫자가 필요한 자리에 문자열 입력
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception
    ) {
        Map<String, Object> details = new LinkedHashMap<>();

        details.put(
                "parameter",
                exception.getName()
        );

        details.put(
                "value",
                exception.getValue()
        );

        if (exception.getRequiredType() != null) {
            details.put(
                    "requiredType",
                    exception.getRequiredType().getSimpleName()
            );
        }

        log.warn(
                "요청 값의 타입이 올바르지 않습니다. parameter={}, value={}, requiredType={}",
                exception.getName(),
                exception.getValue(),
                exception.getRequiredType()
        );

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_REQUEST,
                "요청 값의 형식이 올바르지 않습니다.",
                details,
                exception.getClass(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * 필수 요청 파라미터가 누락된 경우
     *
     * 예:
     * GET /api/channels 요청에서 필수 userId 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception
    ) {
        Map<String, Object> details = new LinkedHashMap<>();

        details.put(
                "parameter",
                exception.getParameterName()
        );

        details.put(
                "type",
                exception.getParameterType()
        );

        log.warn(
                "필수 요청 파라미터가 누락되었습니다. parameter={}, type={}",
                exception.getParameterName(),
                exception.getParameterType()
        );

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_REQUEST,
                "필수 요청 파라미터가 누락되었습니다.",
                details,
                exception.getClass(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * 아직 커스텀 예외로 교체하지 않은
     * 기존 IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        log.warn(
                "잘못된 요청이 발생했습니다. message={}",
                exception.getMessage()
        );

        String message;

        if (exception.getMessage() == null
                || exception.getMessage().isBlank()) {

            message = ErrorCode.INVALID_REQUEST.getMessage();
        } else {
            message = exception.getMessage();
        }

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_REQUEST,
                message,
                Map.of(),
                exception.getClass(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * favicon.ico 등 존재하지 않는 정적 리소스 요청 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException exception
    ) {
        log.debug(
                "정적 리소스를 찾을 수 없습니다. resourcePath={}",
                exception.getResourcePath()
        );

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_REQUEST,
                "요청한 리소스를 찾을 수 없습니다.",
                Map.of(
                        "resourcePath",
                        exception.getResourcePath()
                ),
                exception.getClass(),
                HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /*
     * 위에서 처리하지 못한 모든 예외의 최종 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception
    ) {
        log.error(
                "처리되지 않은 서버 예외가 발생했습니다.",
                exception
        );

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                Map.of(),
                exception.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * DiscodeitException을 ErrorResponse로 변환하는 공통 메서드
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            DiscodeitException exception,
            HttpStatus status
    ) {
        ErrorResponse response = ErrorResponse.from(
                exception,
                status.value()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}