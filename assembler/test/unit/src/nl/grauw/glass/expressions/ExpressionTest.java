package nl.grauw.glass.expressions;

import static org.junit.Assert.*;

import java.io.LineNumberReader;
import java.io.StringReader;

import nl.grauw.glass.Line;
import nl.grauw.glass.LineParser;
import nl.grauw.glass.Scope;

import org.junit.Test;

public class ExpressionTest {
	
	@Test
	public void testPositive() {
		assertEquals(100, parse("+100").getInteger());
	}
	
	@Test
	public void testPositiveTwice() {
		assertEquals(100, parse("++100").getInteger());
	}
	
	@Test
	public void testNegative() {
		assertEquals(-100, parse("-100").getInteger());
	}
	
	@Test
	public void testNegativeTwice() {
		assertEquals(100, parse("--100").getInteger());
	}
	
	@Test
	public void testComplement() {
		assertEquals(-5, parse("~4").getInteger());
	}
	
	@Test
	public void testComplementTwice() {
		assertEquals(4, parse("~~4").getInteger());
	}
	
	@Test
	public void testNot() {
		assertEquals(0, parse("!100").getInteger());
	}
	
	@Test
	public void testNotTwice() {
		assertEquals(-1, parse("!!100").getInteger());
	}
	
	@Test
	public void testMultiply() {
		assertEquals(99, parse("9 * 11").getInteger());
	}
	
	@Test
	public void testDivide() {
		assertEquals(3, parse("11 / 3").getInteger());
	}
	
	@Test(expected=EvaluationException.class)
	public void testDivideByZero() {
		parse("1 / 0").getInteger();
	}
	
	@Test
	public void testModulo() {
		assertEquals(2, parse("11 % 3").getInteger());
	}
	
	@Test(expected=EvaluationException.class)
	public void testModuloByZero() {
		parse("1 % 0").getInteger();
	}
	
	@Test
	public void testAdd() {
		assertEquals(9, parse("4 + 5").getInteger());
	}
	
	@Test
	public void testSubtract() {
		assertEquals(-4, parse("5 - 9").getInteger());
	}
	
	@Test
	public void testShiftLeft() {
		assertEquals(192, parse("3 << 6").getInteger());
	}
	
	@Test
	public void testShiftRight() {
		assertEquals(3, parse("193 >> 6").getInteger());
	}
	
	@Test
	public void testShiftRightSign() {
		assertEquals(-1, parse("-1 >> 16").getInteger());
	}
	
	@Test
	public void testLessThan() {
		assertEquals(-1, parse("3 < 4").getInteger());
		assertEquals(0, parse("4 < 4").getInteger());
		assertEquals(0, parse("5 < 4").getInteger());
	}
	
	@Test
	public void testLessOrEquals() {
		assertEquals(-1, parse("3 <= 4").getInteger());
		assertEquals(-1, parse("4 <= 4").getInteger());
		assertEquals(0, parse("5 <= 4").getInteger());
	}
	
	@Test
	public void testGreaterThan() {
		assertEquals(0, parse("3 > 4").getInteger());
		assertEquals(0, parse("4 > 4").getInteger());
		assertEquals(-1, parse("5 > 4").getInteger());
	}
	
	@Test
	public void testGreaterOrEquals() {
		assertEquals(0, parse("3 >= 4").getInteger());
		assertEquals(-1, parse("4 >= 4").getInteger());
		assertEquals(-1, parse("5 >= 4").getInteger());
	}
	
	@Test
	public void testEquals() {
		assertEquals(0, parse("3 = 4").getInteger());
		assertEquals(-1, parse("4 = 4").getInteger());
		assertEquals(0, parse("5 = 4").getInteger());
	}
	
	@Test
	public void testNotEquals() {
		assertEquals(-1, parse("3 != 4").getInteger());
		assertEquals(0, parse("4 != 4").getInteger());
		assertEquals(-1, parse("5 != 4").getInteger());
	}
	
	@Test
	public void testAnd() {
		assertEquals(2, parse("6 & 3").getInteger());
	}
	
	@Test
	public void testXor() {
		assertEquals(5, parse("6 ^ 3").getInteger());
	}
	
	@Test
	public void testOr() {
		assertEquals(7, parse("6 | 3").getInteger());
	}
	
	@Test
	public void testLogicalAnd() {
		assertEquals(0, parse("0 && 0").getInteger());
		assertEquals(0, parse("0 && 8").getInteger());
		assertEquals(0, parse("-1 && 0").getInteger());
		assertEquals(3, parse("1 && 3").getInteger());
	}
	
	@Test
	public void testLogicalOr() {
		assertEquals(0, parse("0 || 0").getInteger());
		assertEquals(8, parse("0 || 8").getInteger());
		assertEquals(-1, parse("-1 || 0").getInteger());
		assertEquals(1, parse("1 || 3").getInteger());
	}
	
	@Test
	public void testAnnotation() {
		assertEquals("VIRTUAL", parse("VIRTUAL 15").getAnnotation().getName());
		assertEquals(15, parse("VIRTUAL 15").getAnnotee().getInteger());
	}
	
	@Test
	public void testSequence() {
		assertEquals(3, parse("4, 5, 6").getList().size());
		assertEquals(4, parse("4, 5, 6").getElement(0).getInteger());
		assertEquals(5, parse("4, 5, 6").getElement(1).getInteger());
		assertEquals(6, parse("4, 5, 6").getElement(2).getInteger());
		assertEquals(null, parse("4, 5, 6").getElement(3));
	}
	
	@Test
	public void testTernaryIfElse() {
		assertEquals(2, parse("1 ? 2 : 3").getInteger());
		assertEquals(3, parse("0 ? 2 : 3").getInteger());
	}
	
	@Test
	public void testGroup() {
		assertEquals(9, parse("(1 + 2) * 3").getInteger());
	}
	
	public Expression parse(String text) {
		LineNumberReader reader = new LineNumberReader(new StringReader(" test " + text));
		Line line = new LineParser().parse(reader, new Scope(), null);
		return line.getArguments();
	}
	
}
