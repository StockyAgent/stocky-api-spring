package dev.stocky.api.global.fastapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FastApiDeepReportResponse {

  private String status;   // "DONE", "PROCESSING", "ACCEPTED"
  private String symbol;
  private String date;
  private Map<String, Object> data;
  private String message;
}
