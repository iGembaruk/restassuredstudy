package tests.api.utils.dto.generatorproducts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class RatingProduct {

	@JsonProperty("rate")
	private double rate;

	@JsonProperty("count")
	private int count;
}