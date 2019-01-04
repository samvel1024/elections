package sa.elect.json;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionUser;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
@NoArgsConstructor
public class ElectionResponse {
		Integer id;
		LocalDateTime start;
		LocalDateTime end;
		LocalDateTime deadline;
		Integer creatorId;
		String description;
		ElectionStage stage;
		List<ElectionUser> registry;
}
