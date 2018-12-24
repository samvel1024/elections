package sa.elect.json;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CreateUserRequest {
	String first;
	String last;
	String password;
	String studentId;
}
