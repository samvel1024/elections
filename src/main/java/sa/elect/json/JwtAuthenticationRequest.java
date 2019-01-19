package sa.elect.json;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class JwtAuthenticationRequest {
	String studentId;
	String password;
}
