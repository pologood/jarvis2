package com.mogujie.jarvis.core;

import com.google.common.collect.Range;

public class MainTest {

    public static void main(String[] args) {
        Range<Integer> range = Range.closedOpen(1, 5);
        System.out.println(range.lowerEndpoint());
        System.out.println(range.hasLowerBound());
        System.out.println(range.upperEndpoint());
        System.out.println(range.hasUpperBound());
        System.out.println(range.lowerBoundType());
        System.out.println(range.upperBoundType());

    }

}
