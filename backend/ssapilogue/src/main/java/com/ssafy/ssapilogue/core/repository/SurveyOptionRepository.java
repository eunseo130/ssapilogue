package com.ssafy.ssapilogue.core.repository;

import com.ssafy.ssapilogue.core.domain.SurveyOption;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SurveyOptionRepository extends MongoRepository<SurveyOption, String> {
    List<SurveyOption> findAllBySurveyId(String surveyId);
}
