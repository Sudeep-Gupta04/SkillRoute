package com.example.SkillRoute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponseDTO {
    private Long roadmapId;
    private List<String> youtube;
    private Map<String, List<String>> articles;
}