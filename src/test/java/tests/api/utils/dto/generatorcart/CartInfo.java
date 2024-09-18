package tests.api.utils.dto.generatorcart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartInfo {

	@JsonProperty("quantity")
	private int quantity;

	@JsonProperty("productId")
	private int productId;
}