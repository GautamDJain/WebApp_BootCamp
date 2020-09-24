package com;

import org.junit.jupiter.api.Assertions;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;


import com.bootcamp.simple_webapp.MyResource;

@RunWith(JUnitPlatform.class)
public class CalcTest {
 MyResource cal = new MyResource();

	@Test
	public void add() {
		Assertions.assertEquals(40, cal.add());
	}

	@Test
	public void sub() {
		Assertions.assertEquals(0, cal.sub());
	}

	@Test
	public void mul() {
		Assertions.assertEquals(400, cal.mul());
	}

	@Test
	public void div() {
		Assertions.assertEquals(4, cal.div());
	}

}
