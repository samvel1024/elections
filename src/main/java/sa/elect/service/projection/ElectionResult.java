package sa.elect.service.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Data
@ToString(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class ElectionResult {
	@JsonProperty("id")
	public Integer id;
	@JsonProperty("first_name")
	public String first;
	@JsonProperty("last_name")
	public String last;
	@JsonProperty("student_id")
	public String studentId;
	@JsonProperty("vote_count")
	Integer voteCount;
}
