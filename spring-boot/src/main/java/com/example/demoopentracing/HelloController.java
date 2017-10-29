package com.example.demoopentracing;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Pavol Loffay
 */
@RestController
public class HelloController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired private PersonService personService;

    @Autowired private PersonRepository personRepository;

    
    @RequestMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
    
    @RequestMapping("/hello2")
    public String hello2() {
       
    	 ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/hello3", String.class);
         
    	return "Hello2 from Spring Boot!";
    }
    
    @RequestMapping("/hello3")
    public String hello3() {
        return "Hello from Spring Boot!";
    }

    @RequestMapping("/chaining")
    public String chaining() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/hello", String.class);
 ResponseEntity<String> response2 = restTemplate.getForEntity("http://localhost:8080/hello2", String.class);


        return "Chaining + " + response.getBody();
    }
    
    @RequestMapping(value = "/persistPerson", method = RequestMethod.POST)
    public ResponseEntity<String> persistPerson(@RequestBody PersonDTO person) {
      if (personService.isValid(person)) {
        personRepository.persist(person);
        
        ResponseEntity<String> response1 = restTemplate.getForEntity("http://localhost:8080/hello", String.class);
       
        ResponseEntity<String> respons0 = restTemplate.getForEntity("http://localhost:8080/chaining", String.class);
       
	PersonDTO userModel = new PersonDTO( "Mohd", "Shariq", new java.util.Date(),
     "Software Developer", java.math.BigDecimal.ONE);

 //RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
        ResponseEntity<PersonDTO> response = restTemplate.postForEntity("http://localhost:8080/createPerson",
                userModel, PersonDTO.class);
        System.out.println(response.getBody()); 
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
      return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
    }
    
    
    @RequestMapping(value = "/availability", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Boolean isUsernameAvailable(@RequestBody String username) {

        // a load of business logic that returns a boolean
    	System.out.println("Is username available "+ username);
        return Boolean.TRUE;
    }
    
    @RequestMapping(value = "/createPerson", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PersonDTO createUser(@RequestBody PersonDTO userModel)  {

        // a load of business logic that validates the user model

        //RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
        ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8080/availability",
                userModel.getFirstName(), Boolean.class);
        System.out.println(response.getBody());

        // a load more business logic

        return userModel;
    }
    
}
