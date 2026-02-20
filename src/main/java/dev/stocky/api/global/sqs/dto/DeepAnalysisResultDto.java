package dev.stocky.api.global.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeepAnalysisResultDto {

  private String symbol;
  private String reportContent; // Markdown or HTML content
}
