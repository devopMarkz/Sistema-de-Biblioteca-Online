package io.github.devopMarkz.backend.shared.handler;

import io.github.devopMarkz.backend.financas.infraestrutucture.exception.EntidadeInexistenteException;
import io.github.devopMarkz.backend.financas.infraestrutucture.exception.IntegridadeReferencialException;
import io.github.devopMarkz.backend.financas.infraestrutucture.exception.OperacaoInvalidaException;
import io.github.devopMarkz.backend.shared.dto.ErroDTO;
import io.github.devopMarkz.backend.usuario.infraestructure.exception.TokenException;
import io.github.devopMarkz.backend.usuario.infraestructure.exception.UsuarioInativoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IntegridadeReferencialException.class)
    public ResponseEntity<ErroDTO> integridadeReferencialHandler(IntegridadeReferencialException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        ErroDTO erroRespostaDTO = new ErroDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(erroRespostaDTO);
    }

    @ExceptionHandler(OperacaoInvalidaException.class)
    public ResponseEntity<ErroDTO> operacaoInvalidaHandler(OperacaoInvalidaException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        ErroDTO erroRespostaDTO = new ErroDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(erroRespostaDTO);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErroDTO> tokenHandler(TokenException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErroDTO erroDTO = new ErroDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(erroDTO);
    }

    @ExceptionHandler(UsuarioInativoException.class)
    public ResponseEntity<ErroDTO> usuarioInativoHandler(UsuarioInativoException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErroDTO erroDTO = new ErroDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(erroDTO);
    }

    @ExceptionHandler(EntidadeInexistenteException.class)
    public ResponseEntity<ErroDTO> entidadeInexistenteHandler(EntidadeInexistenteException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroDTO erroDTO = new ErroDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(erroDTO);
    }

}
