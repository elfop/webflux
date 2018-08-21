package com.elfop.webflux;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebfluxApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void lbTest(){
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        int[] factor = new int[] { 2 };
        Stream<Integer> stream = numbers.stream()
                .map(e -> e * factor[0]);

//        factor[0] = 0;

        stream.forEach(System.out::println);
    }

}
