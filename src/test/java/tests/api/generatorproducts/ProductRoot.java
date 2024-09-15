package tests.api.generatorproducts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRoot{

	@JsonProperty("image")
	private String image;

	@JsonProperty("price")
	private Double price;

	@JsonProperty("rating")
	private Rating rating;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("category")
	private String category;
}