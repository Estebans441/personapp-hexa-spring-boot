package co.edu.javeriana.as.personapp.domain;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Profession {
	@NonNull
	private Integer identification;
	@NonNull
	private String name;
	private String description;
	@ToString.Exclude
	private List<Study> studies;
}
