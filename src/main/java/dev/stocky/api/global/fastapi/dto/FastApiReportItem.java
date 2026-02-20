package dev.stocky.api.global.fastapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FastApiReportItem {

  private String symbol;
  private String status;
  private String content;
  private Map<String, Object> meta;
}
