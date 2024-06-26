package com.semesterarbeit.quiz.repositories;

import com.semesterarbeit.quiz.models.Question;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {


    @Query(value = "SELECT * FROM question ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandomQuestion();

    @Query(value = "SELECT * FROM question WHERE text = ?1 AND correct_answer = ?2", nativeQuery = true)
    @Nullable
    Optional<Question> getResult(String question, String option);
}
