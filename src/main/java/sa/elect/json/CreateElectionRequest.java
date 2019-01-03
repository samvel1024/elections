package sa.elect.json;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CreateElectionRequest {
	@ApiModelProperty(notes = "Description of the election")
	String desc;
	@ApiModelProperty(notes = "The date up to which candidates can be declared")
	LocalDateTime deadline;
	@ApiModelProperty(notes = "The start date of election")
	LocalDateTime start;
	@ApiModelProperty(notes = "The end date of election ")
	LocalDateTime end;
	@ApiModelProperty(notes = "List of student ids which can participate in the election")
	List<String> registryIds;
}
