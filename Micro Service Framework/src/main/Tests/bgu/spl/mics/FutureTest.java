package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    private Future<String> future;
    @BeforeEach
    void setUp() {
        future =new Future<String>();

    }

    @Test
    void get() {


    }

    @Test
    void resolve() {
        String str ="omar";
        future.resolve(str);
        assertTrue(future.isDone());
        assertEquals(str,future.get());
    }

    @Test
        void isDone() {
        String str ="shahad";
        future.resolve(str);
        assertTrue(future.isDone());

    }

    @Test
    void testGet() {
    }
}