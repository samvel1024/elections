package sa.elect.json;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
@NoArgsConstructor
public class ElectionJson {
		Integer id;
		LocalDateTime start;
		LocalDateTime end;
		LocalDateTime deadline;
		Integer creatorId;
		String description;
		ElectionStage stage;
		List<UserJson> voterRegistry;
}
