package es.ndc.api_rdeco.services;

import es.ndc.api_rdeco.entities.UserEntity;
import es.ndc.api_rdeco.models.PasswordUpdateDto;
import es.ndc.api_rdeco.models.ResponseDto;
import es.ndc.api_rdeco.models.UserDto;
import es.ndc.api_rdeco.models.UserRolesDto;
import es.ndc.api_rdeco.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<UserDto> getUserById(Long userId) {
        try {
            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user.toDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<UserRolesDto> getUserRoles(Long userId) {
        try {
            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user.toRolesDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<UserRolesDto>> getAllUsers() {
        try {
            if (!isADMIN())
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            List<UserEntity> users = userRepository.findAll();
            List<UserRolesDto> userDTOS = new ArrayList<>();
            if (users.isEmpty())
                return ResponseEntity.noContent().build();
            for (UserEntity userEntity : users)
                userDTOS.add(userEntity.toRolesDTO());
            return ResponseEntity.ok(userDTOS);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<ResponseDto> updateUser(Long id, UserEntity updatedUser) {
        ResponseDto response = new ResponseDto();
        try {
            UserEntity previousUser = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            // Solo actualiza el nombre de usuario, correo electrónico e imagen
            previousUser.setName(updatedUser.getName());
            previousUser.setEmail(updatedUser.getEmail());
            previousUser.setImage(updatedUser.getImage());

            userRepository.save(previousUser);
            response.newMessage("Usuario actualizado");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.newError("Id no encontrada");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            response.newError(e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }


    public ResponseEntity<UserDto> updateName(Long id, String nameNew) {
        try {
            UserEntity previousUser = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            previousUser.setName(nameNew);
            final UserEntity user = userRepository.save(previousUser);
            return ResponseEntity.ok(user.toDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<UserDto> updateEmail(Long id, String emailNew) {
        try {
            UserEntity previousUser = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            previousUser.setEmail(emailNew);
            final UserEntity user = userRepository.save(previousUser);
            return ResponseEntity.ok(user.toDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<UserDto> updatePassword(Long id, PasswordUpdateDto passwords) {
        try {
            UserEntity previousUser = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            if (!passwords.getPasswordNew()
                    .matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#ñÑ])[A-Za-z\\d@$!%*?&#ñÑ]{8,}$")
                    || !verifyPassword(passwords.getPasswordOld(), previousUser.getPassword()))
                return new ResponseEntity<>(new UserDto(), HttpStatus.NOT_ACCEPTABLE);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            previousUser.setPassword(encoder.encode(passwords.getPasswordNew()));
            final UserEntity user = userRepository.save(previousUser);
            return ResponseEntity.ok(user.toDTO());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean verifyPassword(String enteredPassword, String storedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(enteredPassword, storedPassword);
    }

    public ResponseEntity<ResponseDto> removeUser(Long userId) {
        ResponseDto response = new ResponseDto();
        try {
            if (!isADMIN())
                return new ResponseEntity<>(new ResponseDto("No tienes permiso"), HttpStatus.UNAUTHORIZED);
            userRepository.deleteById(userId);
            response.newMessage("Usuario borrado");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.newError(e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }

    public boolean isADMIN() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities)
                if ("ADMIN".equals(authority.getAuthority()))
                    return true;
        }
        return false;
    }
}
