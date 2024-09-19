package tests.api.utils.dto.generatorproducts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductRoot{

	@JsonProperty("image")
	private String image;

	@JsonProperty("price")
	private Double price;

	@JsonProperty("rating")
	private RatingProduct ratingProduct;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("category")
	private String category;
}