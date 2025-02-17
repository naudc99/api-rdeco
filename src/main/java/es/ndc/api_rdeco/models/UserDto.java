package es.ndc.api_rdeco.models;


import es.ndc.api_rdeco.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long userId;
    private String name;
    private String email;
    private byte[] image;

    public UserDto(UserEntity user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.image = user.getImage();
    }

}
