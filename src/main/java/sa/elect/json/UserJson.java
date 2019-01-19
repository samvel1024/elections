package sa.elect.json;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import sa.elect.service.projection.Role;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserJson {
	String studentId;
	Role role;
	Integer id;
	String name;
}
