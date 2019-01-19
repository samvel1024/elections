package sa.elect.json;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class JwtAuthenticationResponse {
	String token;
	Integer userId;
}
