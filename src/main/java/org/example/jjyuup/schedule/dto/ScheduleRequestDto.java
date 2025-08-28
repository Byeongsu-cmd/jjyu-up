package org.example.jjyuup.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ScheduleRequestDto {

    @NotBlank
    @Size(min = 1, max = 30,message = "제목은 30자 이하로 작성해 주세요!")
    private String title;

    @NotBlank
    @Size(min = 1, max = 200, message = "내용을 200자 이하로 작성해 주세요!")
    private String content;
}
