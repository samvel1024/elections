package sa.elect.json;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CreateElectionRequest {
	String desc;
	LocalDateTime deadline;
	LocalDateTime start;
	LocalDateTime end;
}
