package com.keskonmange.restcontrollers;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keskonmange.entities.Personne;
import com.keskonmange.enums.Role;
import com.keskonmange.exceptions.ErreurPersonne;
import com.keskonmange.security.jwt.JwtUtils;
import com.keskonmange.security.payload.LoginRequest;
import com.keskonmange.security.response.JwtResponse;
import com.keskonmange.security.services.UserDetailsImpl;
import com.keskonmange.services.ServicePersonne;

@RestController
@CrossOrigin
@RequestMapping("api/personnes")
public class RestControllerPersonne
{
	private String message;

	@Autowired
	ServicePersonne sp;

	@Autowired
	private MessageSource messageSource;	
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtils jwtUtils;

	private void verifPersonne(Integer pid) throws ErreurPersonne {
		if(sp.findById(pid).isEmpty()){
			throw new ErreurPersonne(messageSource.getMessage("erreur.personne.notfound", new Object[]
			{pid}, Locale.getDefault()));
		}
	}
	
	private void verifEmail(String email) throws ErreurPersonne {
		if (!sp.getPersonneByEmail(email).isEmpty()) {
			throw new ErreurPersonne(messageSource.getMessage("erreur.personne.email.found",
					new Object[] { email }, Locale.getDefault()));
		}
	}

	@GetMapping("all")
	public Iterable<Personne> getAll() {
		return sp.findAll();
	}

	@GetMapping("{id}")
	public Optional<Personne> getOne(@PathVariable("id") Integer pid) throws ErreurPersonne {
		verifPersonne(pid);
		return sp.findById(pid);
	}
	
	@GetMapping("/connected")
	public boolean isJwtValid(@RequestHeader(value = "Authorization") String token) {
		if (token.startsWith("Bearer ")) {
			String tokenCut = token.substring(7, token.length());
			return jwtUtils.validateJwtToken(tokenCut);
		}
		return false;
	}


	@PostMapping
	public Personne create(@Valid @RequestBody
	Personne personne, BindingResult result) throws ErreurPersonne {
		if(result.hasErrors())
		{
			message = "";
			result.getFieldErrors().forEach(e ->
			{
				message += messageSource.getMessage("erreur.datalib", new Object[]
				{e.getField(), e.getDefaultMessage()}, Locale.getDefault());
			});
			throw new ErreurPersonne(message);
		}
		return sp.save(personne);
	}

	@PostMapping("/signin")
	public Personne registerUser(@Valid @RequestBody Personne user) throws ErreurPersonne {
		verifEmail(user.getEmail());
		user.setRole(Role.USER);
		user.setPwd(encoder.encode(user.getPwd()));
		return sp.save(user);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest user) throws ErreurPersonne {
		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPwd()));

		} catch (Exception e) {
			throw new ErreurPersonne(messageSource.getMessage("erreur.personne.connectKO", null, Locale.getDefault()));
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles));
	}
	
	
	@PutMapping("{id}")
	public Personne update(@RequestBody Personne personne, @PathVariable("id") Integer pid) throws ErreurPersonne {
		verifPersonne(pid);
		if(pid != personne.getId()){
			throw new ErreurPersonne(messageSource.getMessage("erreur.personne.notequals", new Object[]{pid, personne.getId()}, Locale.getDefault()));
		}
		return sp.save(personne);
	}
	
	@PutMapping("/recalcul")
	public Integer recalcul(@RequestBody Personne personne) throws ErreurPersonne {
		return ServicePersonne.calculBesoinsCaloriques(personne);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Integer pid) throws ErreurPersonne {
		verifPersonne(pid);
		sp.deleteById(pid);
	}
}
