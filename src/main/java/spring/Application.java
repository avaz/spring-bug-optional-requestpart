package spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Anderson
 * <p>
 * In the issue SPR-11829 was introduced support for java.util.Optional for @RequestParam controllers parameters but the
 * @RequestPart was not contemplated with the same feature. When a MVC Controller method parameter is annotated with
 * @RequestPart and is type java.util.Optional if the parameter is not submitted in the request Spring raise the
 * exception: org.springframework.web.multipart.support.MissingServletRequestPartException - "Required request part
 * 'PARAMETER_NAME' is not present."
 * <p>
 * Attached is a github link to a project that demonstrate the bug.
 */
@SpringBootApplication
@RestController
public class Application {

  public static void main( String[] args ) {
    SpringApplication.run( Application.class, args );
  }

  // curl -v -H "Content-Type: multipart/form-data" -F"file=@some-file.jpg" -Fitem='{"name": "Test"};type=application/json' -X POST  http://127.0.0.1:8080/good
  @RequestMapping( value = "/good", method = RequestMethod.POST )
  public ResponseEntity<Item> good( @RequestPart( "item" ) Item item, @RequestParam Optional<MultipartFile> file ) {
    file.ifPresent( System.out::println );
    return new ResponseEntity<>( item, HttpStatus.OK );
  }
  
  // curl -v -H "Content-Type: multipart/form-data" -F"file=@some-file.jpg" -Fitem='{"name": "Test"};type=application/json' -X POST  http://127.0.0.1:8080/bad
  @RequestMapping( value = "/bad", method = RequestMethod.POST )
  public ResponseEntity<Item> bad( @RequestPart Optional<Item> item, @RequestPart Optional<MultipartFile> file ) {
    file.ifPresent( System.out::println );
    return new ResponseEntity<>( item.orElse( new Item() ), HttpStatus.OK );
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
    jsonConverter.setObjectMapper( objectMapper );
    return jsonConverter;
  }
}
