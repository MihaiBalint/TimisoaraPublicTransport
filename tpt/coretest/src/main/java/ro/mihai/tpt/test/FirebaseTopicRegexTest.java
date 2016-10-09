package ro.mihai.tpt.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ro.mihai.util.Formatting;

/**
 * Created by Mihai Balint on 8/23/16.
 */
public class FirebaseTopicRegexTest {

    @Test
    public void test() {
        assertEquals(Formatting.topicSlug("abc"), "abc");
        assertEquals(Formatting.topicSlug("a.bc"), "a.bc");
        assertEquals(Formatting.topicSlug("ab c"), "ab-c");
        assertEquals(Formatting.topicSlug("a  bc"), "a-bc");
        assertEquals(Formatting.topicSlug("a  bc!!!d"), "a-bc-d");
        assertEquals(Formatting.topicSlug("a//bc//"), "a-bc-");
        assertEquals(Formatting.topicSlug("a//bc//"), "a-bc-");
        assertEquals(Formatting.topicSlug("a//bc//  def"), "a-bc-def");
        assertEquals(Formatting.topicSlug("a//bc//"), "a-bc-");
    }
}
