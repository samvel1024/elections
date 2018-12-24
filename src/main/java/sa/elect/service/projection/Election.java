package sa.elect.service.projection;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@ToString(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class Election {
	@JsonProperty("id")
	Integer id;
	@JsonProperty("date_start")
	LocalDateTime start;
	@JsonProperty("date_end")
	LocalDateTime end;
	@JsonProperty("candidate_deadline")
	LocalDateTime deadline;
	@JsonProperty("created_by")
	Integer creatorId;
	@JsonProperty("description")
	String description;
}
