package mediscreen.patient.service;

import mediscreen.patient.model.PatientEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientWithFamilyLikeTest {

    @Mock
    CriteriaBuilder criteriaBuilder;

    @Mock
    Predicate predicate;

    @Mock
    Expression expression;

    @Mock
    Root<PatientEntity> root;

    @Mock
    Path path;

    @Test
    public void givenNullFamily_toPredicate_returnsTruePredicate() {
        // GIVEN
        PatientWithFamilyLike classUnderTest = new PatientWithFamilyLike(null);
        when(criteriaBuilder.isTrue(any(Expression.class))).thenReturn(predicate);
        when(criteriaBuilder.literal(any())).thenReturn(expression);
        // WHEN
        Predicate actualResult = classUnderTest.toPredicate(null, null, criteriaBuilder);
        // THEN
        assertSame(predicate, actualResult);
    }

    @Test
    public void givenFamily_toPredicate_returnsLikePredicate() {
        // GIVEN
        PatientWithFamilyLike classUnderTest = new PatientWithFamilyLike("sample-family");
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(root.get(anyString())).thenReturn(path);
        // WHEN
        Predicate actualResult = classUnderTest.toPredicate(root, null, criteriaBuilder);
        // THEN
        assertSame(predicate, actualResult);
    }
}
