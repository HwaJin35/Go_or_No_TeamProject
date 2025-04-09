package com.goorno.canigo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceMultipartDTO extends CommonMultipartDTO {
	private String name;
	private Double latitude;
	private Double longitude;
}
