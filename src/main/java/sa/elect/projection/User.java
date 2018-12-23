package sa.elect.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Builder
@Data
@ToString(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@JsonProperty("id")
	public Integer id;
	@JsonProperty("first_name")
	public String first;
	@JsonProperty("last_name")
	public String last;
	@JsonProperty("role")
	public Role role;
	@JsonProperty("password")
	public String password;
	@JsonProperty("student_id")
	public String studentId;

}
