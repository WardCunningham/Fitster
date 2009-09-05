// Copyright (c) 2003 Cunningham & Cunningham, Inc.
// Read license.txt in this directory.

import fit.Parse;

import java.text.ParseException;

public class Parster extends Parse implements Cloneable {

    private Parse original;


    // Constructors /////////////////////////////

    public Parster (String tag, String body, Parse parts, Parse more) {
        super (null, null, null, null);
        this.leader = "\n";
        this.tag = "<"+tag+">";
        this.body = body;
        this.end = "</"+tag+">";
        this.trailer = "";
        this.parts = parts;
        this.more = more;
    }

    public Parster (String text) throws ParseException {
        this (text, tags, 0, 0);
    }

    public Parster (String text, String tags[]) throws ParseException {
        this (text, tags, 0, 0);
    }

    public Parster (String text, String tags[], int level, int offset) throws ParseException {
        super (null, null, null, null);

        String lc = text.toLowerCase();
        int startTag = lc.indexOf("<"+tags[level]);
        int endTag = lc.indexOf(">", startTag) + 1;
        int startEnd = lc.indexOf("</"+tags[level], endTag);
        int endEnd = lc.indexOf(">", startEnd) + 1;
        int startMore = lc.indexOf("<"+tags[level], endEnd);
        if (startTag<0 || endTag<0 || startEnd<0 || endEnd<0) {
            throw new ParseException ("Can't find tag: "+tags[level], offset);
        }

        leader = text.substring(0,startTag);
        tag = text.substring(startTag, endTag);
        body = text.substring(endTag, startEnd);
        end = text.substring(startEnd,endEnd);
        trailer = text.substring(endEnd);

        if (level+1 < tags.length) {
            parts = new Parster (body, tags, level+1, offset+endTag);
            body = null;
        }

        if (startMore>=0) {
            more = new Parster (trailer, tags, level, offset+endEnd);
            trailer = null;
        }
    }



    // Annotations //////////////////////////////

    public void addToTag(String text) {
        saveOriginal();
        super.addToTag(text);
    }

    public void addToBody(String text) {
        saveOriginal();
        super.addToBody(text);
    }



    // Utility

    public void saveOriginal() {
        if (original == null) {
            try {
                original = (Parse) this.clone();
            } catch (CloneNotSupportedException e) {
                System.out.println("clone failed in saveOriginal()");
            }
        }
    }

    public void revert() {
        if (original != null) {
            tag = original.tag;
            body = original.body;
            original = null;
        }
        if (!(parts instanceof Parster)) {
            parts = null;
        }
        if (!(more instanceof Parster)) {
            more = null;
        }
    }

    public void revertAll() {
        revert();
        if (parts != null) {
            ((Parster)parts).revertAll();
        }
        if (more != null) {
            ((Parster)more).revertAll();
        }
    }

    public void debug(String pad, Parse node) {
        if (node == null) return;
        System.out.println(pad + node + " " + node.tag);
        debug(pad+"p ", node.parts);
        debug(pad+"m ", node.more);
    }

}
