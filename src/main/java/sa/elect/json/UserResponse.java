package sa.elect.json;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import sa.elect.service.projection.Role;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserResponse {
	String studentId;
	Role role;
	Integer userId;
	String name;
}
