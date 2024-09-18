package tests.api.utils.dto.generatorproducts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingProduct {

	@JsonProperty("rate")
	private Double rate;

	@JsonProperty("count")
	private Integer count;
}