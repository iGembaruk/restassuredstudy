package tests.api.utils.dto.generatoruser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NameUser {

	@JsonProperty("firstname")
	private String firstname;

	@JsonProperty("lastname")
	private String lastname;
}