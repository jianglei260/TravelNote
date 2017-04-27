package cn.edu.cuit.wsy.travelnote;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public int a=123;
    @Test
    public void addition_isCorrect() throws Exception {
        Field field=ExampleUnitTest.class.getField("a");
        System.out.print(field.getType().getName());
    }
}