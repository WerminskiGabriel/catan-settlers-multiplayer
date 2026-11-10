package pl.GabrielW.catan_server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( IllegalStateException.class )
    public ResponseEntity< Map< String, String > > handleIllegalState( IllegalStateException e ) {
        return ResponseEntity
                .status( 400 )
                .body( Map.of( "error" , e.getMessage( ) ) );
    }

    @ExceptionHandler( NoSuchElementException.class )
    public ResponseEntity< Map< String, String > > handleNoSuchElement( NoSuchElementException e ) {
        return ResponseEntity
                .status( 404 )
                .body( Map.of( "error" , e.getMessage( ) ) );
    }

    @ExceptionHandler( IllegalArgumentException.class )
    public ResponseEntity< Map< String, String > > handleIllegalArguments( IllegalArgumentException e ) {
        return ResponseEntity
                .status( 400 )
                .body( Map.of( "error" , e.getMessage( ) ) );
    }
}