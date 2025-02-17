package es.ndc.api_rdeco.services;

import es.ndc.api_rdeco.entities.RoleEntity;
import es.ndc.api_rdeco.entities.UserEntity;
import es.ndc.api_rdeco.models.JwtTokenDto;
import es.ndc.api_rdeco.models.LoginDto;
import es.ndc.api_rdeco.models.ResponseDto;
import es.ndc.api_rdeco.repositories.RoleRepository;
import es.ndc.api_rdeco.repositories.UserRepository;
import es.ndc.api_rdeco.security.JwtUtilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

@Service
public class AuthService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtilityService jwtUtilityService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, JwtUtilityService jwtUtilityService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtilityService = jwtUtilityService;
    }
    public ResponseEntity<List<String>> getAllUserNames() {
        try {
            List<UserEntity> users = userRepository.findAll();
            List<String> names = new ArrayList<>();
            if (users.isEmpty())
                return ResponseEntity.noContent().build();
            for (UserEntity userEntity : users)
                names.add(userEntity.getName());
            return ResponseEntity.ok(names);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    public ResponseEntity<JwtTokenDto> login(LoginDto login) throws Exception {
        try {
            Optional<UserEntity> userOPT = userRepository.findByEmail(login.getEmail());
            if (userOPT.isEmpty())
                return ResponseEntity.notFound().build();
            else {
                UserEntity user = userOPT.get();
                if (verifyPassword(login.getPassword(), user.getPassword())) {
                    JwtTokenDto jwtTokenDTO = new JwtTokenDto(jwtUtilityService.generateJWT(user.getId(),  user.getRole()));
                    return ResponseEntity.ok(jwtTokenDTO);
                } else
                    return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean verifyPassword(String enteredPassword, String storedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(enteredPassword, storedPassword);
    }

    public ResponseEntity<ResponseDto> register(UserEntity userNew, BindingResult result) {
        ResponseDto response = new ResponseDto();
        try {
            if (result != null && result.hasErrors()) {
                for (FieldError error : result.getFieldErrors())
                    response.newError(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
            Optional<UserEntity> existingUser = userRepository.findByName(userNew.getName());
            if (existingUser.isPresent()) {
                response.newError("Nombre en uso, por favor elija otro");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            existingUser = userRepository.findByEmail(userNew.getEmail());
            if (existingUser.isPresent()) {
                response.newError("Email ya registrado");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            UserEntity userTEMP = getTemplateUser();
            Optional<UserEntity> userOPT = updateTemplateUser(userTEMP, userNew);
            if (userOPT.isEmpty())
                return ResponseEntity.unprocessableEntity().build();
            response.newMessage("Usuario creado");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private UserEntity getTemplateUser() {
        int threshold = 3600;
        Optional<UserEntity> userTEMP = userRepository.findFirstUpdatable(threshold);
        if (userTEMP.isEmpty()) {
            UserEntity userEntity = new UserEntity();
            userEntity.setName("NombreNoValido");
            userEntity.setEmail("emailn@oval.ido");
            userEntity.setPassword("ContraseñaNoValida");
            userEntity = userRepository.save(userEntity);
            return userEntity;
        }
        return userTEMP.get();
    }

    private Optional<UserEntity> updateTemplateUser(UserEntity userTemplate, UserEntity updatedUser) {
        try {
            userTemplate.setLifeSpan(updatedUser.getLifeSpan().plusYears(100));
            userTemplate.setName(updatedUser.getName());
            userTemplate.setEmail(updatedUser.getEmail());
            if (!updatedUser.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#ñÑ])[A-Za-z\\d@$!%*?&#ñÑ]{8,}$"))
                return Optional.empty();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            userTemplate.setPassword(encoder.encode(updatedUser.getPassword()));
            Optional<RoleEntity> roleOTP = roleRepository.findByName("USER");
            if(roleOTP.isEmpty())
                return Optional.empty();
            RoleEntity rol = new RoleEntity();
            rol=roleOTP.get();
            userTemplate.setRole(rol);

            UserEntity savedUser = userRepository.save(userTemplate);
            userRepository.save(userTemplate);

            return Optional.of(savedUser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
