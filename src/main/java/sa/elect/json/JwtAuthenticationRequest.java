package sa.elect.json;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class JwtAuthenticationRequest {
	String email;
	String password;
}
