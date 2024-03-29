package task.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import task.manager.entity.Role;
import task.manager.entity.RoleName;
import task.manager.entity.User;
import task.manager.entity.repository.RolesRepository;
import task.manager.entity.repository.UsersRepository;
import task.manager.security.UserDetailsImpl;
import task.manager.security.jwt.JwtResponse;
import task.manager.security.jwt.JwtUtils;
import task.manager.security.jwt.MessageResponse;
import task.manager.service.UsersService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersService usersService;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is required."));
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is required."));
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!userDetails.isActive()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User is not activated!"));
        }

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is required."));
        }
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is required."));
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is required."));
        }
        if (registerRequest.getName() == null || registerRequest.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Name is required."));
        }
        if (registerRequest.getSurname() == null || registerRequest.getSurname().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Surname is required."));
        }

        if (usersRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (usersRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()),
                registerRequest.getName(),
                registerRequest.getSurname());

        Set<String> strRoles = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = rolesRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = rolesRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = rolesRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        usersService.createUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/activate/{userLinkId}")
    public ResponseEntity<String> activateUser(@PathVariable UUID userLinkId) {
        if (usersService.activateUserBy(userLinkId)) {
            return ResponseEntity.ok("User activated");
        }
        return ResponseEntity.badRequest().build();
    }
}
