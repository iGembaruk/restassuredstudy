package tests.api.utils.dto.generatorcart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class CartInfo {

	@JsonProperty("quantity")
	private int quantity;

	@JsonProperty("productId")
	private int productId;
}