package com.solo.semi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SemiApplicationTests {

	@Autowired
	private MemberMapper memberMapper;

	@Test
	void contextLoads() {
	}

}
