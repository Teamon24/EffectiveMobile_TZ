package org.effective_mobile.task_management_system.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.stream.Collector;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Headers extends ArrayList<Headers.Header> {

    public static final String START_TIME_HEADER = "start-time";
    public static final Collector<Header, Headers, Headers> pairsToHeaders =
        Collector.of(
            Headers::new,
            (Headers headers, Header pair) -> headers.add(pair.getLeft(), pair.getRight()),
            Headers::add);

    public Headers add(String key, String value) {
        super.add(Header.of(key, value));
        return this;
    }

    public Headers add(Headers headers) {
        this.addAll(headers);
        return this;
    }

    public static Headers empty() {
        return new Headers();
    }

    public static class Header extends ImmutablePair<String, String> {
        public static final Header EMPTY_HEADER = new Header();
        private Header() { super(null, null); }
        public Header(String left, String right) { super(left, right); }
        public static Header of(final String left, final String right) {
            return left != null || right != null ? new Header(left, right) : EMPTY_HEADER;
        }
    }
}
