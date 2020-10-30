package mediscreen.patient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class PatientApplicationTest {

	@Autowired
	PatientApplication classUnderTest;

	@Test
	void contextLoads() {
		String[] noParameters = {};
		classUnderTest.main(noParameters);
	}

}
