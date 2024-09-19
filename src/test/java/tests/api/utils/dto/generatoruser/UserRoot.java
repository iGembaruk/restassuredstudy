package tests.api.utils.dto.generatoruser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserRoot{

	@JsonProperty("password")
	private String password;

	@JsonProperty("address")
	private AddressUser addressUser;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("__v")
	private int v;

	@JsonProperty("name")
	private NameUser nameUser;

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("email")
	private String email;

	@JsonProperty("username")
	private String username;
}