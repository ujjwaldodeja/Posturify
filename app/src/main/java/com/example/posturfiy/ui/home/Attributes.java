package com.example.posturfiy.ui.home;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;

public class Attributes {
//    public final Attribute LEFT_DIFF = new Attribute("left_diff");
//    public final Attribute RIGHT_DIFF = new Attribute("right_diff");
    public final Attribute LEFT_DIFF = new Attribute("left_avg");
    public final Attribute RIGHT_DIFF = new Attribute("right_avg");
    public final Attribute VERT_DIFF = new Attribute("vert_diff");
    private final List<String> classes = new ArrayList<String>() {
        {
            add("straight");
            add("right");
            add("left");
        }
    };

    public Attribute getLEFT_DIFF() {
        return LEFT_DIFF;
    }

    public Attribute getRIGHT_DIFF() {
        return RIGHT_DIFF;
    }

    public Attribute getVERT_DIFF() {
        return VERT_DIFF;
    }

    public void setAttributeList(ArrayList<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<String> getClasses() {
        return classes;
    }

    private ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2) {
        {
            add(LEFT_DIFF);
            add(RIGHT_DIFF);
            add(VERT_DIFF);
            Attribute attributeClass = new Attribute("@@class@@", classes);
            add(attributeClass);
        }
    };

    public  ArrayList<Attribute> getAttributeList() {
        return attributeList;
    }
}

